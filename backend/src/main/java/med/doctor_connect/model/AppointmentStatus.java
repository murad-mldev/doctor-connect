package med.doctor_connect.model;

public enum AppointmentStatus {
    PENDING,
    CONFIRMED,
    PATIENT_WAITING,           // Patient joined waiting room
    IN_PROGRESS,               // Consultation in progress
    COMPLETED,
    CANCELLED,
    NO_SHOW,                   // Patient didn't show up
    NEXT_PATIENT_NOTIFIED      // Next patient notified but hasn't joined yet
}
