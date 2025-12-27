package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreateNotificationRequest;
import med.doctor_connect.dto.NotificationDto;
import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.Notification;
import med.doctor_connect.model.NotificationType;
import med.doctor_connect.model.User;
import med.doctor_connect.repository.AppointmentRepository;
import med.doctor_connect.repository.NotificationRepository;
import med.doctor_connect.repository.UserRepository;
import med.doctor_connect.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public NotificationDto createNotification(CreateNotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .type(request.getType())
                .title(request.getTitle())
                .body(request.getBody())
                .metadata(request.getMetadata())
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return buildNotificationDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(UUID userId, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, limit);
        Page<Notification> notifications = notificationRepository.findByUser(user, pageable);
        return notifications.map(this::buildNotificationDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUnreadNotifications(UUID userId, int page, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, limit);
        Page<Notification> notifications = notificationRepository.findByUserAndIsReadFalse(user, pageable);
        return notifications.map(this::buildNotificationDto);
    }

    @Override
    public NotificationDto markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        return buildNotificationDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    public void sendAppointmentCreatedNotification(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Notify patient
        CreateNotificationRequest patientNotification = CreateNotificationRequest.builder()
                .userId(appointment.getPatient().getUser().getId())
                .type(NotificationType.APPOINTMENT_CREATED)
                .title("Appointment Created")
                .body("Your appointment with Dr. " + appointment.getDoctor().getUser().getFullName() + " has been created.")
                .build();
        createNotification(patientNotification);

        // Notify doctor
        CreateNotificationRequest doctorNotification = CreateNotificationRequest.builder()
                .userId(appointment.getDoctor().getUser().getId())
                .type(NotificationType.APPOINTMENT_CREATED)
                .title("New Appointment")
                .body("New appointment scheduled with " + appointment.getPatient().getUser().getFullName())
                .build();
        createNotification(doctorNotification);
    }

    private NotificationDto buildNotificationDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId().toString())
                .userId(notification.getUser().getId().toString())
                .type(notification.getType())
                .title(notification.getTitle())
                .body(notification.getBody())
                .metadata(notification.getMetadata())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
