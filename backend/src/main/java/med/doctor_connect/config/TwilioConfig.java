package med.doctor_connect.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TwilioConfig {

    @Value("${notification.sms.twilio.account-sid}")
    private String accountSid;

    @Value("${notification.sms.twilio.auth-token}")
    private String authToken;

    @Value("${notification.sms.enabled}")
    private boolean smsEnabled;

    @PostConstruct
    public void initTwilio() {
        if (smsEnabled && !accountSid.equals("your-account-sid")) {
            try {
                Twilio.init(accountSid, authToken);
                log.info("Twilio initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Twilio: " + e.getMessage());
            }
        } else {
            log.info("Twilio SMS is disabled or not configured");
        }
    }
}
