package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreateVideoRoomRequest;
import med.doctor_connect.dto.VideoRoomDto;
import med.doctor_connect.service.VideoConsultationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
public class VideoConsultationController {

    private final VideoConsultationService videoConsultationService;

    @PostMapping("/rooms")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<VideoRoomDto> createVideoRoom(@RequestBody CreateVideoRoomRequest request) {
        VideoRoomDto videoRoom = videoConsultationService.createVideoRoom(request);
        return new ResponseEntity<>(videoRoom, HttpStatus.CREATED);
    }

    @GetMapping("/rooms/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<VideoRoomDto> getVideoRoomByAppointment(@PathVariable String appointmentId) {
        UUID appId = UUID.fromString(appointmentId);
        VideoRoomDto videoRoom = videoConsultationService.getVideoRoomByAppointmentId(appId);
        return ResponseEntity.ok(videoRoom);
    }

    @GetMapping("/rooms/{roomId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<VideoRoomDto> getVideoRoomById(@PathVariable String roomId) {
        UUID rid = UUID.fromString(roomId);
        VideoRoomDto videoRoom = videoConsultationService.getVideoRoomById(rid);
        return ResponseEntity.ok(videoRoom);
    }

    @PostMapping("/rooms/{roomId}/token")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<Map<String, String>> generateJoinToken(
            @PathVariable String roomId,
            @RequestBody Map<String, String> request) {

        UUID rid = UUID.fromString(roomId);
        String participantName = request.get("participantName");

        String token = videoConsultationService.generateJoinToken(rid, participantName);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("roomId", roomId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/rooms/{roomId}/end")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> endVideoRoom(@PathVariable String roomId) {
        UUID rid = UUID.fromString(roomId);
        videoConsultationService.endVideoRoom(rid);
        return ResponseEntity.noContent().build();
    }
}
