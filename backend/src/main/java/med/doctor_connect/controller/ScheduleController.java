package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreateScheduleSlotRequest;
import med.doctor_connect.dto.ScheduleSlotDto;
import med.doctor_connect.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/doctors/{doctorId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Public endpoint - Get doctor's schedule slots (for visitors and logged-in users)
     * Returns only available slots (not fully booked)
     */
    @GetMapping
    public ResponseEntity<List<ScheduleSlotDto>> getDoctorSchedules(
            @PathVariable String doctorId,
            @RequestParam(required = false) String date) {

        UUID docId = UUID.fromString(doctorId);
        LocalDate scheduleDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        List<ScheduleSlotDto> slots = scheduleService.getDoctorSchedules(docId, scheduleDate);
        return ResponseEntity.ok(slots);
    }

    /**
     * Public endpoint - Get available (not fully booked) schedule slots for a doctor
     */
    @GetMapping("/available")
    public ResponseEntity<List<ScheduleSlotDto>> getAvailableSchedules(
            @PathVariable String doctorId,
            @RequestParam(required = false) String date) {

        UUID docId = UUID.fromString(doctorId);
        LocalDate scheduleDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        List<ScheduleSlotDto> slots = scheduleService.getAvailableSchedules(docId, scheduleDate);
        return ResponseEntity.ok(slots);
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ScheduleSlotDto> createScheduleSlot(
            @PathVariable String doctorId,
            @RequestBody CreateScheduleSlotRequest request) {

        UUID docId = UUID.fromString(doctorId);
        ScheduleSlotDto slot = scheduleService.createScheduleSlot(docId, request);
        return new ResponseEntity<>(slot, HttpStatus.CREATED);
    }

    @PutMapping("/{slotId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ScheduleSlotDto> updateScheduleSlot(
            @PathVariable String doctorId,
            @PathVariable String slotId,
            @RequestBody CreateScheduleSlotRequest request) {

        UUID docId = UUID.fromString(doctorId);
        UUID scheduleSlotId = UUID.fromString(slotId);

        ScheduleSlotDto slot = scheduleService.updateScheduleSlot(docId, scheduleSlotId, request);
        return ResponseEntity.ok(slot);
    }

    @DeleteMapping("/{slotId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteScheduleSlot(
            @PathVariable String doctorId,
            @PathVariable String slotId) {

        UUID docId = UUID.fromString(doctorId);
        UUID scheduleSlotId = UUID.fromString(slotId);

        scheduleService.deleteScheduleSlot(docId, scheduleSlotId);
        return ResponseEntity.noContent().build();
    }
}
