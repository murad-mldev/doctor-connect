package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't throw exception
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendAppointmentConfirmationEmail(String to, String patientName, String doctorName, String appointmentTime) {
        String subject = "Appointment Confirmation - Doctor Connect";
        String body = String.format(
                "Dear %s,\n\n" +
                "Your appointment with Dr. %s has been confirmed.\n\n" +
                "Appointment Time: %s\n\n" +
                "Please arrive 10 minutes before your scheduled time.\n\n" +
                "Thank you,\n" +
                "Doctor Connect Team",
                patientName, doctorName, appointmentTime
        );

        sendEmail(to, subject, body);
    }

    @Override
    public void sendAppointmentCancellationEmail(String to, String patientName, String doctorName, String appointmentTime) {
        String subject = "Appointment Cancelled - Doctor Connect";
        String body = String.format(
                "Dear %s,\n\n" +
                "Your appointment with Dr. %s scheduled for %s has been cancelled.\n\n" +
                "If you need to reschedule, please contact us or book a new appointment.\n\n" +
                "Thank you,\n" +
                "Doctor Connect Team",
                patientName, doctorName, appointmentTime
        );

        sendEmail(to, subject, body);
    }
}
