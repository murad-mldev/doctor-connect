package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.dto.UserDto;
import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.AppointmentStatus;
import med.doctor_connect.repository.AppointmentRepository;
import med.doctor_connect.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/video-queue")
@RequiredArgsConstructor
@Slf4j
public class VideoConsultationQueueController {

    private final AppointmentQueueService queueService;
    private final TwilioVideoService twilioVideoService;
    private final ConsultationAnalyticsService analyticsService;
    private final UserService userService;
    private final ProfileService profileService;
    private final AppointmentRepository appointmentRepository;

    // ============= PATIENT ENDPOINTS =============

    @PostMapping("/join-waiting-room/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> joinWaitingRoom(@PathVariable String appointmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID aptId = UUID.fromString(appointmentId);
        Map<String, Object> response = queueService.joinWaitingRoom(aptId, user.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-position/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> getMyQueuePosition(@PathVariable String appointmentId) {
        UUID aptId = UUID.fromString(appointmentId);
        Map<String, Object> response = queueService.getQueuePosition(aptId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/join-consultation/{appointmentId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> joinConsultation(@PathVariable String appointmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID aptId = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(aptId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate patient is authorized
        if (!appointment.getPatient().getUser().getId().toString().equals(user.getId())) {
            throw new RuntimeException("Not authorized for this consultation");
        }

        // Validate consultation is in progress
        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS &&
            appointment.getStatus() != AppointmentStatus.NEXT_PATIENT_NOTIFIED) {
            throw new RuntimeException("Consultation not started yet");
        }

        // Generate access token
        String accessToken = twilioVideoService.generateAccessToken(aptId, user.getId(), "patient");

        Map<String, Object> response = new HashMap<>();
        response.put("appointmentId", appointmentId);
        response.put("twilioRoomName", appointment.getTwilioRoomName());
        response.put("accessToken", accessToken);
        response.put("doctorName", appointment.getDoctor().getUser().getFullName());

        return ResponseEntity.ok(response);
    }

    // ============= DOCTOR ENDPOINTS =============

    @GetMapping("/my-queue")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> getMyQueue() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID userId = UUID.fromString(user.getId());
        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        Map<String, Object> response = queueService.getDoctorQueue(doctorProfileId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/waiting-patients")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<Appointment>> getWaitingPatients() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID userId = UUID.fromString(user.getId());
        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        List<Appointment> waiting = queueService.getWaitingPatients(doctorProfileId);

        return ResponseEntity.ok(waiting);
    }

    @PostMapping("/start-consultation/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> startConsultation(@PathVariable String appointmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID aptId = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(aptId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate doctor owns this appointment
        if (!appointment.getDoctor().getUser().getId().toString().equals(user.getId())) {
            throw new RuntimeException("Not authorized for this appointment");
        }

        // Create Twilio room
        String roomSid = twilioVideoService.createVideoRoom(aptId);

        // Update appointment status
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointment.setConsultationStartedAt(new Date());
        appointmentRepository.save(appointment);

        // Generate doctor access token
        String accessToken = twilioVideoService.generateAccessToken(aptId, user.getId(), "doctor");

        Map<String, Object> response = new HashMap<>();
        response.put("appointmentId", appointmentId);
        response.put("twilioRoomName", appointment.getTwilioRoomName());
        response.put("twilioRoomSid", roomSid);
        response.put("accessToken", accessToken);
        response.put("status", "IN_PROGRESS");
        response.put("patientName", appointment.getPatient().getUser().getFullName());

        log.info("Doctor {} started consultation for appointment {}", user.getId(), appointmentId);

        // TODO: Send WebSocket notification to patient
        // TODO: Send SMS notification to patient

        return ResponseEntity.ok(response);
    }

    @PostMapping("/end-consultation/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> endConsultation(@PathVariable String appointmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID aptId = UUID.fromString(appointmentId);
        Appointment appointment = appointmentRepository.findById(aptId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        UUID userId = UUID.fromString(user.getId());
        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        // Validate doctor owns this appointment
        if (!appointment.getDoctor().getId().equals(doctorProfileId)) {
            throw new RuntimeException("Not authorized for this appointment");
        }

        // End Twilio room
        if (appointment.getTwilioRoomSid() != null) {
            twilioVideoService.completeRoom(appointment.getTwilioRoomSid());
        }

        // Calculate duration
        if (appointment.getConsultationStartedAt() != null) {
            long durationMinutes = (new Date().getTime() - appointment.getConsultationStartedAt().getTime()) / 60000;
            appointment.setActualDurationMinutes((int) durationMinutes);

            // Update doctor's average duration
            analyticsService.updateDoctorAverageDuration(doctorProfileId, (int) durationMinutes);
        }

        // Update status
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setConsultationEndedAt(new Date());
        appointment.setQueuePosition(null);
        appointmentRepository.save(appointment);

        // Get next patient in queue
        Appointment nextPatient = queueService.getNextPatientInQueue(doctorProfileId);

        Map<String, Object> response = new HashMap<>();
        response.put("appointmentId", appointmentId);
        response.put("status", "COMPLETED");
        response.put("durationMinutes", appointment.getActualDurationMinutes());

        if (nextPatient != null) {
            queueService.notifyNextPatient(doctorProfileId);

            Map<String, Object> nextPatientInfo = new HashMap<>();
            nextPatientInfo.put("appointmentId", nextPatient.getId().toString());
            nextPatientInfo.put("patientName", nextPatient.getPatient().getUser().getFullName());
            nextPatientInfo.put("notificationSent", true);

            response.put("nextPatient", nextPatientInfo);
        } else {
            response.put("nextPatient", null);
        }

        // Update queue positions
        queueService.updateQueuePositions(doctorProfileId);

        log.info("Doctor {} ended consultation for appointment {}", doctorProfileId, appointmentId);

        // TODO: Send WebSocket notification to next patient
        // TODO: Send SMS notification to next patient

        return ResponseEntity.ok(response);
    }

    @PostMapping("/notify-next-patient")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> notifyNextPatient() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID userId = UUID.fromString(user.getId());
        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        queueService.notifyNextPatient(doctorProfileId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Next patient notified successfully");

        return ResponseEntity.ok(response);
    }

    // ============= ANALYTICS ENDPOINTS =============

    @GetMapping("/analytics/doctor-stats")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> getMyStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);

        UUID userId = UUID.fromString(user.getId());
        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        Map<String, Object> stats = analyticsService.getDoctorStats(doctorProfileId);

        return ResponseEntity.ok(stats);
    }
}
