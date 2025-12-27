package med.doctor_connect.service;

import med.doctor_connect.dto.CreateNotificationRequest;
import med.doctor_connect.dto.NotificationDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface NotificationService {

    NotificationDto createNotification(CreateNotificationRequest request);

    Page<NotificationDto> getUserNotifications(UUID userId, int page, int limit);

    Page<NotificationDto> getUnreadNotifications(UUID userId, int page, int limit);

    NotificationDto markAsRead(UUID notificationId);

    long getUnreadCount(UUID userId);

    void sendAppointmentCreatedNotification(UUID appointmentId);
}
