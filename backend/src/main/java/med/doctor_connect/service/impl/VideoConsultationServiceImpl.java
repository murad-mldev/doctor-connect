package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.CreateVideoRoomRequest;
import med.doctor_connect.dto.VideoRoomDto;
import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.VideoRoom;
import med.doctor_connect.repository.AppointmentRepository;
import med.doctor_connect.repository.VideoRoomRepository;
import med.doctor_connect.service.VideoConsultationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VideoConsultationServiceImpl implements VideoConsultationService {

    private final VideoRoomRepository videoRoomRepository;
    private final AppointmentRepository appointmentRepository;

    @Value("${video.google-meet.enabled}")
    private boolean meetEnabled;

    @Value("${video.google-meet.project-id}")
    private String projectId;

    @Override
    public VideoRoomDto createVideoRoom(CreateVideoRoomRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Check if room already exists
        Optional<VideoRoom> existing = videoRoomRepository.findByAppointment(appointment);
        if (existing.isPresent()) {
            return buildVideoRoomDto(existing.get());
        }

        // For development: Generate a simple meeting link
        // In production, this would call Google Meet API
        String meetingCode = generateMeetingCode();
        String meetUrl = generateMeetUrl(meetingCode);

        // Set expiration to 24 hours from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 24);
        Date expiresAt = cal.getTime();

        VideoRoom videoRoom = VideoRoom.builder()
                .appointment(appointment)
                .meetUrl(meetUrl)
                .meetingCode(meetingCode)
                .conferenceId(UUID.randomUUID().toString())
                .isActive(true)
                .expiresAt(expiresAt)
                .build();

        VideoRoom saved = videoRoomRepository.save(videoRoom);

        log.info("Video room created for appointment: {} with meeting code: {}",
                appointment.getId(), meetingCode);

        return buildVideoRoomDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoRoomDto getVideoRoomByAppointmentId(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        VideoRoom videoRoom = videoRoomRepository.findByAppointment(appointment)
                .orElseThrow(() -> new RuntimeException("Video room not found for this appointment"));

        return buildVideoRoomDto(videoRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoRoomDto getVideoRoomById(UUID roomId) {
        VideoRoom videoRoom = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Video room not found"));

        return buildVideoRoomDto(videoRoom);
    }

    @Override
    public String generateJoinToken(UUID roomId, String participantName) {
        VideoRoom videoRoom = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Video room not found"));

        if (!videoRoom.isActive()) {
            throw new RuntimeException("Video room is not active");
        }

        // For development: Return a simple token
        // In production, this would generate a proper Google Meet access token
        String token = Base64.getEncoder().encodeToString(
                (participantName + ":" + videoRoom.getMeetingCode() + ":" + System.currentTimeMillis())
                        .getBytes()
        );

        log.info("Generated join token for participant: {} in room: {}", participantName, roomId);

        return token;
    }

    @Override
    public void endVideoRoom(UUID roomId) {
        VideoRoom videoRoom = videoRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Video room not found"));

        videoRoom.setActive(false);
        videoRoomRepository.save(videoRoom);

        log.info("Video room ended: {}", roomId);
    }

    private String generateMeetingCode() {
        // Generate a random 10-character meeting code
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                code.append(chars.charAt(random.nextInt(chars.length())));
            }
            if (i < 2) code.append("-");
        }

        return code.toString();
    }

    private String generateMeetUrl(String meetingCode) {
        // For development: Generate a Google Meet-like URL
        // In production, this would be the actual Google Meet API response
        return "https://meet.google.com/" + meetingCode.replace("-", "");
    }

    private VideoRoomDto buildVideoRoomDto(VideoRoom videoRoom) {
        return VideoRoomDto.builder()
                .id(videoRoom.getId().toString())
                .appointmentId(videoRoom.getAppointment().getId().toString())
                .meetUrl(videoRoom.getMeetUrl())
                .meetingCode(videoRoom.getMeetingCode())
                .conferenceId(videoRoom.getConferenceId())
                .isActive(videoRoom.isActive())
                .expiresAt(videoRoom.getExpiresAt())
                .createdAt(videoRoom.getCreatedAt())
                .build();
    }
}
