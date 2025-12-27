package med.doctor_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "schedule_slot",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_doctor_date_time",
            columnNames = {"doctor_id", "date", "start_time", "end_time"})
    },
    indexes = {
        @Index(name = "idx_schedule_doctor_date", columnList = "doctor_id, date")
    }
)
@RequiredArgsConstructor
@AllArgsConstructor
public class ScheduleSlot {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    private int capacity = 1;

    @Column(name = "booked_count", nullable = false)
    @Builder.Default
    private int bookedCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
