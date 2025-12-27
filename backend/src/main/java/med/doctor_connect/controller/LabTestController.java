package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.LabTestDto;
import med.doctor_connect.service.LabTestService;
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
@RequestMapping("/api/v1/admin/lab-tests")
@RequiredArgsConstructor
public class LabTestController {

    private final LabTestService labTestService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabTestDto> createLabTest(@RequestBody LabTestDto labTestDto) {
        LabTestDto created = labTestService.createLabTest(labTestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabTestDto> getLabTestById(@PathVariable String id) {
        UUID labTestId = UUID.fromString(id);
        LabTestDto labTest = labTestService.getLabTestById(labTestId);
        return ResponseEntity.ok(labTest);
    }

    @GetMapping("/active")
    public ResponseEntity<List<LabTestDto>> getAllActiveLabTests() {
        List<LabTestDto> labTests = labTestService.getAllActiveLabTests();
        return ResponseEntity.ok(labTests);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> searchLabTests(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Page<LabTestDto> labTests = labTestService.searchLabTests(name, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", labTests.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", labTests.getNumber());
        meta.put("limit", labTests.getSize());
        meta.put("totalElements", labTests.getTotalElements());
        meta.put("totalPages", labTests.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabTestDto> updateLabTest(
            @PathVariable String id,
            @RequestBody LabTestDto labTestDto) {

        UUID labTestId = UUID.fromString(id);
        LabTestDto updated = labTestService.updateLabTest(labTestId, labTestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLabTest(@PathVariable String id) {
        UUID labTestId = UUID.fromString(id);
        labTestService.deleteLabTest(labTestId);
        return ResponseEntity.noContent().build();
    }
}
