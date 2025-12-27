package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.AppointmentDto;
import med.doctor_connect.dto.CreateAppointmentRequest;
import med.doctor_connect.dto.UpdateAppointmentStatusRequest;
import med.doctor_connect.mapper.AppointmentMapper;
import med.doctor_connect.model.*;
import med.doctor_connect.repository.*;
import med.doctor_connect.service.AppointmentService;
import med.doctor_connect.service.EmailService;
import med.doctor_connect.service.SMSService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SMSService smsService;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        // Get entities
        ScheduleSlot slot = scheduleSlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Schedule slot not found"));

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        PatientProfile patient = patientProfileRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Check slot capacity
        if (slot.getBookedCount() >= slot.getCapacity()) {
            throw new RuntimeException("Slot capacity reached");
        }

        // Check if doctor matches slot
        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Slot does not belong to this doctor");
        }

        // Create appointment time from slot
        LocalDateTime appointmentDateTime = LocalDateTime.of(slot.getDate(), slot.getStartTime());
        Date appointmentTime = Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Create appointment
        Appointment appointment = Appointment.builder()
                .slot(slot)
                .doctor(doctor)
                .patient(patient)
                .status(AppointmentStatus.PENDING)
                .appointmentTime(appointmentTime)
                .reason(request.getReason())
                .build();

        // Increment booked count
        slot.setBookedCount(slot.getBookedCount() + 1);
        scheduleSlotRepository.save(slot);

        Appointment saved = appointmentRepository.save(appointment);

        // Send notifications
        sendAppointmentNotifications(saved, "created");

        return appointmentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentDto getAppointmentById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return appointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional
    public Page<AppointmentDto> getUserAppointments(UUID userId, AppointmentStatus status, Date fromDate, Date toDate, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);

        // Use very old/future dates as defaults to avoid PostgreSQL parameter type inference issues
        Date effectiveFromDate = fromDate != null ? fromDate : new Date(0L); // Jan 1, 1970
        // Use year 2999 as far future (PostgreSQL timestamp max is year 294276)
        Date effectiveToDate = toDate != null ? toDate : new Date(32503680000000L); // Dec 31, 2999

        // Try to find patient profile
        PatientProfile patient = patientProfileRepository.findByUserId(userId).orElse(null);
        if (patient == null) {
            // Try to find doctor profile
            DoctorProfile doctor = doctorProfileRepository.findByUserId(userId).orElse(null);
            if (doctor != null) {
                Page<Appointment> appointments = appointmentRepository.findDoctorAppointments(
                    doctor, status, effectiveFromDate, effectiveToDate, pageable);
                return appointments.map(appointmentMapper::toDto);
            }

            // Profile doesn't exist - auto-create patient profile
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            patient = PatientProfile.builder()
                .user(user)
                .build();
            patient = patientProfileRepository.save(patient);
        }

        Page<Appointment> appointments = appointmentRepository.findPatientAppointments(
            patient, status, effectiveFromDate, effectiveToDate, pageable);
        return appointments.map(appointmentMapper::toDto);
    }

    @Override
    public AppointmentDto updateAppointmentStatus(UUID appointmentId, UpdateAppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(request.getStatus());

        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(updated);
    }

    @Override
    public AppointmentDto cancelAppointment(UUID appointmentId, String cancellationReason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel appointment with status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        // Decrement booked count
        ScheduleSlot slot = appointment.getSlot();
        if (slot.getBookedCount() > 0) {
            slot.setBookedCount(slot.getBookedCount() - 1);
            scheduleSlotRepository.save(slot);
        }

        Appointment updated = appointmentRepository.save(appointment);

        // Send cancellation notifications
        sendAppointmentNotifications(updated, "cancelled");

        return appointmentMapper.toDto(updated);
    }

    @Override
    public AppointmentDto rescheduleAppointment(UUID appointmentId, UUID newSlotId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        ScheduleSlot newSlot = scheduleSlotRepository.findById(newSlotId)
                .orElseThrow(() -> new RuntimeException("New schedule slot not found"));

        // Check new slot capacity
        if (newSlot.getBookedCount() >= newSlot.getCapacity()) {
            throw new RuntimeException("New slot capacity reached");
        }

        // Decrement old slot count
        ScheduleSlot oldSlot = appointment.getSlot();
        if (oldSlot.getBookedCount() > 0) {
            oldSlot.setBookedCount(oldSlot.getBookedCount() - 1);
            scheduleSlotRepository.save(oldSlot);
        }

        // Increment new slot count
        newSlot.setBookedCount(newSlot.getBookedCount() + 1);
        scheduleSlotRepository.save(newSlot);

        // Update appointment
        appointment.setSlot(newSlot);
        LocalDateTime appointmentDateTime = LocalDateTime.of(newSlot.getDate(), newSlot.getStartTime());
        Date appointmentTime = Date.from(appointmentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.PENDING);

        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(updated);
    }

    private void sendAppointmentNotifications(Appointment appointment, String action) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            String appointmentTime = sdf.format(appointment.getAppointmentTime());

            String patientName = appointment.getPatient().getUser().getFullName();
            String doctorName = appointment.getDoctor().getUser().getFullName();
            String patientEmail = appointment.getPatient().getUser().getEmail();
            String patientPhone = appointment.getPatient().getUser().getPhone();
            String doctorEmail = appointment.getDoctor().getUser().getEmail();

            if ("created".equals(action)) {
                // Send to patient
                if (patientEmail != null && !patientEmail.isEmpty()) {
                    emailService.sendAppointmentConfirmationEmail(patientEmail, patientName, doctorName, appointmentTime);
                }
                if (patientPhone != null && !patientPhone.isEmpty()) {
                    smsService.sendAppointmentConfirmationSms(patientPhone, patientName, doctorName, appointmentTime);
                }

                // Notify doctor
                if (doctorEmail != null && !doctorEmail.isEmpty()) {
                    emailService.sendEmail(doctorEmail,
                            "New Appointment Booked",
                            String.format("You have a new appointment with %s scheduled for %s.", patientName, appointmentTime));
                }
            } else if ("cancelled".equals(action)) {
                // Send to patient
                if (patientEmail != null && !patientEmail.isEmpty()) {
                    emailService.sendAppointmentCancellationEmail(patientEmail, patientName, doctorName, appointmentTime);
                }
                if (patientPhone != null && !patientPhone.isEmpty()) {
                    smsService.sendAppointmentCancellationSms(patientPhone, patientName, doctorName, appointmentTime);
                }

                // Notify doctor
                if (doctorEmail != null && !doctorEmail.isEmpty()) {
                    emailService.sendEmail(doctorEmail,
                            "Appointment Cancelled",
                            String.format("Your appointment with %s scheduled for %s has been cancelled.", patientName, appointmentTime));
                }
            }
        } catch (Exception e) {
            // Log but don't fail the transaction
            System.err.println("Failed to send appointment notifications: " + e.getMessage());
        }
    }
}
