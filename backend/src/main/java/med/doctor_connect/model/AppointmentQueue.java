package med.doctor_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "appointment_queue", indexes = {
    @Index(name = "idx_queue_doctor_date", columnList = "doctor_id, queue_date")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class AppointmentQueue {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @OneToOne
    @JoinColumn(name = "current_appointment_id")
    private Appointment currentAppointment;

    @Column(name = "queue_date", nullable = false)
    private Date queueDate;

    @Column(name = "total_waiting_patients")
    @Builder.Default
    private Integer totalWaitingPatients = 0;

    @Column(name = "average_wait_time_minutes")
    private Integer averageWaitTimeMinutes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
