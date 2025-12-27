package med.doctor_connect.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendAppointmentConfirmationEmail(String to, String patientName, String doctorName, String appointmentTime);

    void sendAppointmentCancellationEmail(String to, String patientName, String doctorName, String appointmentTime);
}
