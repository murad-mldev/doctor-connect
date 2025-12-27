package med.doctor_connect.service;

public interface SMSService {

    void sendSms(String to, String message);

    void sendAppointmentConfirmationSms(String to, String patientName, String doctorName, String appointmentTime);

    void sendAppointmentCancellationSms(String to, String patientName, String doctorName, String appointmentTime);

    void sendAppointmentReminderSms(String to, String patientName, String doctorName, String appointmentTime);
}
