package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.*;
import med.doctor_connect.model.AppointmentStatus;
import med.doctor_connect.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody CreateAppointmentRequest request) {
        AppointmentDto appointment = appointmentService.createAppointment(request);
        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable String id) {
        UUID appointmentId = UUID.fromString(id);
        AppointmentDto appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/users/{userId}/appointments")
    public ResponseEntity<Map<String, Object>> getUserAppointments(
            @PathVariable String userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID uid = UUID.fromString(userId);
        AppointmentStatus appointmentStatus = status != null ? AppointmentStatus.valueOf(status) : null;

        Date fromDate = null;
        Date toDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (from != null) {
                fromDate = sdf.parse(from);
            }
            if (to != null) {
                toDate = sdf.parse(to);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use yyyy-MM-dd");
        }

        Page<AppointmentDto> appointments = appointmentService.getUserAppointments(uid, appointmentStatus, fromDate, toDate, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", appointments.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", appointments.getNumber());
        meta.put("limit", appointments.getSize());
        meta.put("totalElements", appointments.getTotalElements());
        meta.put("totalPages", appointments.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/appointments/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(
            @PathVariable String id,
            @RequestBody UpdateAppointmentStatusRequest request) {

        UUID appointmentId = UUID.fromString(id);
        AppointmentDto appointment = appointmentService.updateAppointmentStatus(appointmentId, request);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/appointments/{id}/cancel")
    public ResponseEntity<AppointmentDto> cancelAppointment(
            @PathVariable String id,
            @RequestBody CancelAppointmentRequest request) {

        UUID appointmentId = UUID.fromString(id);
        AppointmentDto appointment = appointmentService.cancelAppointment(appointmentId, request.getCancellationReason());
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/appointments/{id}/reschedule")
    public ResponseEntity<AppointmentDto> rescheduleAppointment(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {

        UUID appointmentId = UUID.fromString(id);
        UUID newSlotId = UUID.fromString(request.get("newSlotId"));

        AppointmentDto appointment = appointmentService.rescheduleAppointment(appointmentId, newSlotId);
        return ResponseEntity.ok(appointment);
    }
}
