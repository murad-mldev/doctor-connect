package med.doctor_connect.service.impl;

import com.twilio.Twilio;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;
import com.twilio.rest.video.v1.Room;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.model.Appointment;
import med.doctor_connect.repository.AppointmentRepository;
import med.doctor_connect.service.TwilioVideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Slf4j
public class TwilioVideoServiceImpl implements TwilioVideoService {

    private final AppointmentRepository appointmentRepository;

    @Value("${notification.sms.twilio.account-sid}")
    private String accountSid;

    @Value("${notification.sms.twilio.auth-token}")
    private String authToken;

    @Value("${twilio.api.key:#{null}}")
    private String apiKey;

    @Value("${twilio.api.secret:#{null}}")
    private String apiSecret;

    public TwilioVideoServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @PostConstruct
    private void initializeTwilio() {
        Twilio.init(accountSid, authToken);
        log.info("Twilio initialized successfully");
    }

    @Override
    @Transactional
    public String createVideoRoom(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Check if room already exists
        if (appointment.getTwilioRoomSid() != null) {
            log.info("Room already exists for appointment: {}", appointmentId);
            return appointment.getTwilioRoomSid();
        }

        try {
            // Create unique room name
            String roomName = "appointment-" + appointmentId.toString();

            // Create Twilio Video Room
            Room room = Room.creator()
                    .setUniqueName(roomName)
                    .setType(Room.RoomType.PEER_TO_PEER) // P2P for 1-on-1, more secure
                    .setMaxParticipants(2) // Only doctor and patient
                    .create();

            // Save room details
            appointment.setTwilioRoomSid(room.getSid());
            appointment.setTwilioRoomName(roomName);
            appointmentRepository.save(appointment);

            log.info("Created Twilio room {} for appointment {}", room.getSid(), appointmentId);
            return room.getSid();

        } catch (Exception e) {
            log.error("Error creating Twilio room for appointment {}: {}", appointmentId, e.getMessage(), e);
            throw new RuntimeException("Failed to create video room: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateAccessToken(UUID appointmentId, String userId, String participantType) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Validate participant
        boolean isDoctor = appointment.getDoctor().getUser().getId().toString().equals(userId);
        boolean isPatient = appointment.getPatient().getUser().getId().toString().equals(userId);

        if (!isDoctor && !isPatient) {
            throw new RuntimeException("User not authorized for this consultation");
        }

        if (appointment.getTwilioRoomName() == null) {
            throw new RuntimeException("Video room not created yet");
        }

        try {
            // Use API key for token generation (more secure than account credentials)
            String keyToUse = apiKey != null ? apiKey : accountSid;
            String secretToUse = apiSecret != null ? apiSecret : authToken;

            // Create identity
            String identity = participantType + "-" + userId;

            // Create Video Grant
            VideoGrant grant = new VideoGrant()
                    .setRoom(appointment.getTwilioRoomName());

            // Create Access Token (secret needs to be byte[] in Twilio SDK 10.x)
            AccessToken token = new AccessToken.Builder(
                    accountSid,
                    keyToUse,
                    secretToUse.getBytes(StandardCharsets.UTF_8)
            )
                    .identity(identity)
                    .grant(grant)
                    .ttl(7200) // 2 hours
                    .build();

            String jwt = token.toJwt();

            log.info("Generated access token for {} in appointment {}", participantType, appointmentId);
            return jwt;

        } catch (Exception e) {
            log.error("Error generating access token for appointment {}: {}", appointmentId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate access token: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void completeRoom(String roomSid) {
        try {
            Room room = Room.updater(roomSid, Room.RoomStatus.COMPLETED).update();
            log.info("Completed Twilio room: {}", roomSid);

        } catch (Exception e) {
            log.error("Error completing room {}: {}", roomSid, e.getMessage(), e);
            throw new RuntimeException("Failed to complete room: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isRoomActive(String roomSid) {
        try {
            Room room = Room.fetcher(roomSid).fetch();
            return room.getStatus() == Room.RoomStatus.IN_PROGRESS;

        } catch (Exception e) {
            log.error("Error checking room status {}: {}", roomSid, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getRoomSid(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        return appointment.getTwilioRoomSid();
    }
}
