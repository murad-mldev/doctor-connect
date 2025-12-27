package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.model.*;
import med.doctor_connect.repository.AppointmentQueueRepository;
import med.doctor_connect.repository.AppointmentRepository;
import med.doctor_connect.repository.DoctorConsultationStatsRepository;
import med.doctor_connect.repository.DoctorProfileRepository;
import med.doctor_connect.service.AppointmentQueueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentQueueServiceImpl implements AppointmentQueueService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentQueueRepository appointmentQueueRepository;
    private final DoctorConsultationStatsRepository statsRepository;

    @Override
    @Transactional
    public Map<String, Object> joinWaitingRoom(UUID appointmentId, String userId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate user is the patient for this appointment
        if (!appointment.getPatient().getUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Not authorized for this appointment");
        }

        // Check if already in waiting room or in progress
        if (appointment.getStatus() == AppointmentStatus.PATIENT_WAITING ||
            appointment.getStatus() == AppointmentStatus.IN_PROGRESS) {
            throw new RuntimeException("Already in waiting room or consultation");
        }

        // Update appointment status
        appointment.setStatus(AppointmentStatus.PATIENT_WAITING);
        appointment.setPatientJoinedAt(new Date());

        // Calculate queue position based on appointment time (FIFO)
        updateQueuePositions(appointment.getDoctor().getId());

        appointmentRepository.save(appointment);

        // Calculate estimated wait time
        Integer estimatedWait = calculateEstimatedWaitTime(appointmentId);

        Map<String, Object> response = new HashMap<>();
        response.put("appointmentId", appointmentId.toString());
        response.put("status", "PATIENT_WAITING");
        response.put("queuePosition", appointment.getQueuePosition());
        response.put("estimatedWaitMinutes", estimatedWait);
        response.put("message", appointment.getQueuePosition() == 1 ?
                "You're next in queue" :
                "Doctor is currently with another patient. You're " + appointment.getQueuePosition() + " in queue.");

        log.info("Patient {} joined waiting room for appointment {}", userId, appointmentId);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDoctorQueue(UUID doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Get current consultation (IN_PROGRESS)
        Optional<Appointment> currentConsultation = appointmentRepository
                .findByDoctorAndStatus(doctor, AppointmentStatus.IN_PROGRESS)
                .stream()
                .findFirst();

        // Get waiting patients ordered by appointment time (FIFO)
        List<Appointment> waitingPatients = getWaitingPatients(doctorId);

        // Get doctor's average consultation time
        Integer avgMinutes = statsRepository.findByDoctorId(doctorId)
                .map(DoctorConsultationStats::getAverageConsultationMinutes)
                .orElse(15);

        Map<String, Object> response = new HashMap<>();

        // Current consultation details
        if (currentConsultation.isPresent()) {
            Appointment current = currentConsultation.get();
            long durationMinutes = (new Date().getTime() - current.getConsultationStartedAt().getTime()) / 60000;

            Map<String, Object> currentInfo = new HashMap<>();
            currentInfo.put("appointmentId", current.getId().toString());
            currentInfo.put("patientName", current.getPatient().getUser().getFullName());
            currentInfo.put("durationMinutes", durationMinutes);
            currentInfo.put("status", "IN_PROGRESS");

            response.put("currentConsultation", currentInfo);
        } else {
            response.put("currentConsultation", null);
        }

        // Waiting queue
        List<Map<String, Object>> queueList = waitingPatients.stream()
                .map(apt -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("appointmentId", apt.getId().toString());
                    item.put("patientName", apt.getPatient().getUser().getFullName());
                    item.put("scheduledTime", apt.getAppointmentTime());
                    item.put("queuePosition", apt.getQueuePosition());

                    if (apt.getPatientJoinedAt() != null) {
                        long waitMinutes = (new Date().getTime() - apt.getPatientJoinedAt().getTime()) / 60000;
                        item.put("waitingMinutes", waitMinutes);
                    } else {
                        item.put("waitingMinutes", 0);
                    }

                    return item;
                })
                .collect(Collectors.toList());

        response.put("waitingQueue", queueList);
        response.put("totalWaiting", waitingPatients.size());
        response.put("averageConsultationMinutes", avgMinutes);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getQueuePosition(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Integer estimatedWait = calculateEstimatedWaitTime(appointmentId);

        Map<String, Object> response = new HashMap<>();
        response.put("queuePosition", appointment.getQueuePosition());
        response.put("estimatedWaitMinutes", estimatedWait);
        response.put("status", appointment.getStatus());

        if (appointment.getPatientJoinedAt() != null) {
            long waitedMinutes = (new Date().getTime() - appointment.getPatientJoinedAt().getTime()) / 60000;
            response.put("waitedMinutes", waitedMinutes);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getWaitingPatients(UUID doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Get all waiting and notified patients, ordered by appointment time (FIFO)
        List<Appointment> appointments = appointmentRepository.findByDoctorAndStatusIn(
                doctor,
                Arrays.asList(
                        AppointmentStatus.PATIENT_WAITING,
                        AppointmentStatus.NEXT_PATIENT_NOTIFIED,
                        AppointmentStatus.CONFIRMED
                )
        );

        // Sort by appointment time (First In First Out)
        appointments.sort(Comparator.comparing(Appointment::getAppointmentTime));

        return appointments;
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getNextPatientInQueue(UUID doctorId) {
        List<Appointment> waiting = getWaitingPatients(doctorId);

        if (waiting.isEmpty()) {
            return null;
        }

        // First patient in FIFO queue
        return waiting.get(0);
    }

    @Override
    @Transactional
    public void updateQueuePositions(UUID doctorId) {
        List<Appointment> waitingPatients = getWaitingPatients(doctorId);

        // Assign queue positions based on FIFO (appointment time)
        for (int i = 0; i < waitingPatients.size(); i++) {
            Appointment apt = waitingPatients.get(i);
            apt.setQueuePosition(i + 1);
            appointmentRepository.save(apt);
        }

        log.info("Updated queue positions for doctor {}: {} patients", doctorId, waitingPatients.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateEstimatedWaitTime(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        UUID doctorId = appointment.getDoctor().getId();

        // Get average consultation time
        Integer avgMinutes = statsRepository.findByDoctorId(doctorId)
                .map(DoctorConsultationStats::getAverageConsultationMinutes)
                .orElse(15);

        // Get current consultation
        Optional<Appointment> current = appointmentRepository
                .findByDoctorAndStatus(appointment.getDoctor(), AppointmentStatus.IN_PROGRESS)
                .stream()
                .findFirst();

        int estimatedWait = 0;

        // If there's a current consultation, estimate remaining time
        if (current.isPresent()) {
            Appointment currentApt = current.get();
            long elapsedMinutes = (new Date().getTime() - currentApt.getConsultationStartedAt().getTime()) / 60000;
            int remainingMinutes = Math.max(0, avgMinutes - (int) elapsedMinutes);
            estimatedWait += remainingMinutes;
        }

        // Add wait time for patients ahead in queue
        Integer queuePosition = appointment.getQueuePosition();
        if (queuePosition != null && queuePosition > 1) {
            estimatedWait += (queuePosition - 1) * avgMinutes;
        }

        return estimatedWait;
    }

    @Override
    @Transactional
    public void notifyNextPatient(UUID doctorId) {
        Appointment nextPatient = getNextPatientInQueue(doctorId);

        if (nextPatient == null) {
            log.info("No patients waiting for doctor {}", doctorId);
            return;
        }

        nextPatient.setStatus(AppointmentStatus.NEXT_PATIENT_NOTIFIED);
        nextPatient.setNotifiedAt(new Date());
        appointmentRepository.save(nextPatient);

        log.info("Notified next patient {} for doctor {}", nextPatient.getId(), doctorId);

        // TODO: Send WebSocket notification
        // TODO: Send SMS notification
    }

    @Override
    @Transactional
    public void removeFromQueue(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        UUID doctorId = appointment.getDoctor().getId();

        appointment.setQueuePosition(null);
        appointmentRepository.save(appointment);

        // Update positions for remaining patients
        updateQueuePositions(doctorId);

        log.info("Removed appointment {} from queue", appointmentId);
    }
}
