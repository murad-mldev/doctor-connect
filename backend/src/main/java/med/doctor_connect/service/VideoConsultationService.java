package med.doctor_connect.service;

import med.doctor_connect.dto.CreateVideoRoomRequest;
import med.doctor_connect.dto.VideoRoomDto;

import java.util.UUID;

public interface VideoConsultationService {

    VideoRoomDto createVideoRoom(CreateVideoRoomRequest request);

    VideoRoomDto getVideoRoomByAppointmentId(UUID appointmentId);

    VideoRoomDto getVideoRoomById(UUID roomId);

    String generateJoinToken(UUID roomId, String participantName);

    void endVideoRoom(UUID roomId);
}
