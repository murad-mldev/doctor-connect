package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.AdminStatsDto;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminStatController {

    private final AdminService adminService;

    @GetMapping("/doctors/pending")
    public ResponseEntity<Map<String, Object>> getPendingDoctorVerifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        Page<DoctorProfileDto> doctors = adminService.getPendingDoctorVerifications(page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", doctors.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", doctors.getNumber());
        meta.put("limit", doctors.getSize());
        meta.put("totalElements", doctors.getTotalElements());
        meta.put("totalPages", doctors.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/doctors/{id}/verify")
    public ResponseEntity<DoctorProfileDto> verifyDoctor(@PathVariable String id) {
        UUID doctorId = UUID.fromString(id);
        DoctorProfileDto doctor = adminService.verifyDoctor(doctorId);
        return ResponseEntity.ok(doctor);
    }

    @PostMapping("/doctors/{id}/reject")
    public ResponseEntity<DoctorProfileDto> rejectDoctor(@PathVariable String id) {
        UUID doctorId = UUID.fromString(id);
        DoctorProfileDto doctor = adminService.rejectDoctor(doctorId);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getSystemStats() {
        AdminStatsDto stats = adminService.getSystemStats();
        return ResponseEntity.ok(stats);
    }
}
