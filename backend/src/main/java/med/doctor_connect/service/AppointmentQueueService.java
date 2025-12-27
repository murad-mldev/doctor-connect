package med.doctor_connect.service;

import med.doctor_connect.model.Appointment;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AppointmentQueueService {

    Map<String, Object> joinWaitingRoom(UUID appointmentId, String userId);

    Map<String, Object> getDoctorQueue(UUID doctorId);

    Map<String, Object> getQueuePosition(UUID appointmentId);

    List<Appointment> getWaitingPatients(UUID doctorId);

    Appointment getNextPatientInQueue(UUID doctorId);

    void updateQueuePositions(UUID doctorId);

    Integer calculateEstimatedWaitTime(UUID appointmentId);

    void notifyNextPatient(UUID doctorId);

    void removeFromQueue(UUID appointmentId);
}
