package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.NotificationDto;
import med.doctor_connect.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID uid = UUID.fromString(userId);
        Page<NotificationDto> notifications = notificationService.getUserNotifications(uid, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", notifications.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", notifications.getNumber());
        meta.put("limit", notifications.getSize());
        meta.put("totalElements", notifications.getTotalElements());
        meta.put("totalPages", notifications.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit) {

        UUID uid = UUID.fromString(userId);
        Page<NotificationDto> notifications = notificationService.getUnreadNotifications(uid, page, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("data", notifications.getContent());

        Map<String, Object> meta = new HashMap<>();
        meta.put("page", notifications.getNumber());
        meta.put("limit", notifications.getSize());
        meta.put("totalElements", notifications.getTotalElements());
        meta.put("totalPages", notifications.getTotalPages());
        response.put("meta", meta);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable String id) {
        UUID notificationId = UUID.fromString(id);
        NotificationDto notification = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String userId) {
        UUID uid = UUID.fromString(userId);
        long count = notificationService.getUnreadCount(uid);

        Map<String, Long> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}
