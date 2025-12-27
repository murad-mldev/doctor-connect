package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import med.doctor_connect.model.NotificationType;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    private UUID userId;
    private NotificationType type;
    private String title;
    private String body;
    private String metadata;
}
