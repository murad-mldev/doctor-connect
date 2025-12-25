package med.doctor_connect.config;

import jakarta.servlet.SessionCookieConfig;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    /**
     * Configure session cookie for localhost development
     * Removes SameSite attribute to work around browser restrictions
     */
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig cookieConfig = servletContext.getSessionCookieConfig();
            cookieConfig.setHttpOnly(false); // Allow JavaScript access for debugging
            cookieConfig.setSecure(false);
            cookieConfig.setPath("/");
            cookieConfig.setMaxAge(1800); // 30 minutes
        };
    }
}
