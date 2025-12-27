package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.MedicalHistoryDto;
import med.doctor_connect.service.MedicalHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/medical-history")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT', 'ADMIN')")
    public ResponseEntity<MedicalHistoryDto> getPatientMedicalHistory(@PathVariable String patientId) {
        UUID pid = UUID.fromString(patientId);
        MedicalHistoryDto history = medicalHistoryService.getPatientMedicalHistory(pid);
        return ResponseEntity.ok(history);
    }
}
