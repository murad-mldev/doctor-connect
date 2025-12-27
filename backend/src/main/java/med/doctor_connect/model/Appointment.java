package med.doctor_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "appointment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_slot_patient", columnNames = {"slot_id", "patient_id"})
    },
    indexes = {
        @Index(name = "idx_appointment_doctor_date", columnList = "doctor_id, appointment_time"),
        @Index(name = "idx_appointment_patient", columnList = "patient_id"),
        @Index(name = "idx_appointment_slot_patient", columnList = "slot_id, patient_id")
    }
)
@RequiredArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private ScheduleSlot slot;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(name = "appointment_time", nullable = false)
    private Date appointmentTime;

    @Column(name = "reason")
    private String reason;

    @Column(name = "fee", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;

    // Queue and Video Consultation fields
    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "patient_joined_at")
    private Date patientJoinedAt;

    @Column(name = "consultation_started_at")
    private Date consultationStartedAt;

    @Column(name = "consultation_ended_at")
    private Date consultationEndedAt;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    @Column(name = "twilio_room_sid")
    private String twilioRoomSid;

    @Column(name = "twilio_room_name")
    private String twilioRoomName;

    @Column(name = "notified_at")
    private Date notifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
