package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Public endpoints accessible to visitors (no authentication required)
 */
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicController {

    private final DoctorService doctorService;

    /**
     * Get all approved doctors (for visitors to browse)
     * Only returns doctors who are verified and approved
     */
    @GetMapping("/doctors")
    public ResponseEntity<Map<String, Object>> getAvailableDoctors(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID deptId = departmentId != null ? UUID.fromString(departmentId) : null;

        Page<DoctorProfileDto> doctors = doctorService.searchDoctors(deptId, specialization, name, page, limit);

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

    /**
     * Get single doctor profile by ID (for visitors to view details)
     */
    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<DoctorProfileDto> getDoctorById(@PathVariable String doctorId) {
        UUID docId = UUID.fromString(doctorId);
        DoctorProfileDto doctor = doctorService.getDoctorById(docId);
        return ResponseEntity.ok(doctor);
    }
}
