package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreatePrescriptionRequest;
import med.doctor_connect.dto.PrescriptionDto;
import med.doctor_connect.service.PrescriptionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/appointments/{id}/prescriptions")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<PrescriptionDto> createPrescription(
            @PathVariable String id,
            @RequestBody CreatePrescriptionRequest request) {

        UUID appointmentId = UUID.fromString(id);
        PrescriptionDto prescription = prescriptionService.createPrescription(appointmentId, request);
        return new ResponseEntity<>(prescription, HttpStatus.CREATED);
    }

    @GetMapping("/prescriptions/{id}")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable String id) {
        UUID prescriptionId = UUID.fromString(id);
        PrescriptionDto prescription = prescriptionService.getPrescriptionById(prescriptionId);
        return ResponseEntity.ok(prescription);
    }

    @GetMapping("/patients/{patientId}/prescriptions")
    public ResponseEntity<Map<String, Object>> getPatientPrescriptions(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID pid = UUID.fromString(patientId);
        Page<PrescriptionDto> prescriptions = prescriptionService.getPatientPrescriptions(pid, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", prescriptions.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", prescriptions.getNumber());
        meta.put("limit", prescriptions.getSize());
        meta.put("totalElements", prescriptions.getTotalElements());
        meta.put("totalPages", prescriptions.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }
}
