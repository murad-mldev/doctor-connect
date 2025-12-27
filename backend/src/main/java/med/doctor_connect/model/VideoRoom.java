package med.doctor_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "video_room", indexes = {
    @Index(name = "idx_video_room_appointment", columnList = "appointment_id")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class VideoRoom {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(name = "meet_url", nullable = false)
    private String meetUrl;

    @Column(name = "meeting_code")
    private String meetingCode;

    @Column(name = "conference_id")
    private String conferenceId;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "expires_at")
    private Date expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
}
