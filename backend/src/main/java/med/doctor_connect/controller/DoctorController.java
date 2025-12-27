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

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> searchDoctors(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID departmentId = department != null ? UUID.fromString(department) : null;
        Page<DoctorProfileDto> doctors = doctorService.searchDoctors(departmentId, specialization, name, page, limit);

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

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctorById(@PathVariable String id) {
        UUID doctorId = UUID.fromString(id);
        DoctorProfileDto doctor = doctorService.getDoctorById(doctorId);

        boolean hasAvailability = doctorService.hasAvailableSlots(doctorId);

        Map<String, Object> response = new HashMap<>();
        response.put("doctor", doctor);
        response.put("hasAvailability", hasAvailability);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Map<String, Object>> checkDoctorAvailability(@PathVariable String id) {
        UUID doctorId = UUID.fromString(id);
        boolean available = doctorService.hasAvailableSlots(doctorId);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);

        return ResponseEntity.ok(response);
    }
}
