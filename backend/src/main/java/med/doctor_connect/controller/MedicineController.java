package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.MedicineDto;
import med.doctor_connect.service.MedicineService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineDto> createMedicine(@RequestBody MedicineDto medicineDto) {
        MedicineDto created = medicineService.createMedicine(medicineDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineDto> getMedicineById(@PathVariable String id) {
        UUID medicineId = UUID.fromString(id);
        MedicineDto medicine = medicineService.getMedicineById(medicineId);
        return ResponseEntity.ok(medicine);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MedicineDto>> getAllActiveMedicines() {
        List<MedicineDto> medicines = medicineService.getAllActiveMedicines();
        return ResponseEntity.ok(medicines);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> searchMedicines(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Page<MedicineDto> medicines = medicineService.searchMedicines(name, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", medicines.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", medicines.getNumber());
        meta.put("limit", medicines.getSize());
        meta.put("totalElements", medicines.getTotalElements());
        meta.put("totalPages", medicines.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineDto> updateMedicine(
            @PathVariable String id,
            @RequestBody MedicineDto medicineDto) {

        UUID medicineId = UUID.fromString(id);
        MedicineDto updated = medicineService.updateMedicine(medicineId, medicineDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicine(@PathVariable String id) {
        UUID medicineId = UUID.fromString(id);
        medicineService.deleteMedicine(medicineId);
        return ResponseEntity.noContent().build();
    }
}
