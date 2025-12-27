package med.doctor_connect.service.impl;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.service.SMSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSServiceImpl implements SMSService {

    @Value("${notification.sms.twilio.from-number}")
    private String fromNumber;

    @Value("${notification.sms.enabled}")
    private boolean smsEnabled;

    @Override
    public void sendSms(String to, String message) {
        if (!smsEnabled) {
            log.info("SMS is disabled. Would send to {}: {}", to, message);
            return;
        }

        try {
            Message twilioMessage = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    message
            ).create();

            log.info("SMS sent successfully. SID: {}", twilioMessage.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
            // Don't throw exception - SMS is not critical
        }
    }

    @Override
    public void sendAppointmentConfirmationSms(String to, String patientName, String doctorName, String appointmentTime) {
        String message = String.format(
                "Dear %s, your appointment with Dr. %s is confirmed for %s. " +
                "Please arrive 10 minutes early. - Doctor Connect",
                patientName, doctorName, appointmentTime
        );
        sendSms(to, message);
    }

    @Override
    public void sendAppointmentCancellationSms(String to, String patientName, String doctorName, String appointmentTime) {
        String message = String.format(
                "Dear %s, your appointment with Dr. %s scheduled for %s has been cancelled. " +
                "Contact us to reschedule. - Doctor Connect",
                patientName, doctorName, appointmentTime
        );
        sendSms(to, message);
    }

    @Override
    public void sendAppointmentReminderSms(String to, String patientName, String doctorName, String appointmentTime) {
        String message = String.format(
                "Reminder: Dear %s, you have an appointment with Dr. %s at %s tomorrow. " +
                "Please be on time. - Doctor Connect",
                patientName, doctorName, appointmentTime
        );
        sendSms(to, message);
    }
}
