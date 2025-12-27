package med.doctor_connect.service;

import java.util.UUID;

public interface TwilioVideoService {

    String createVideoRoom(UUID appointmentId);

    String generateAccessToken(UUID appointmentId, String userId, String participantType);

    void completeRoom(String roomSid);

    boolean isRoomActive(String roomSid);

    String getRoomSid(UUID appointmentId);
}
