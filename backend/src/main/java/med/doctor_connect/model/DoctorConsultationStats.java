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
@Table(name = "doctor_consultation_stats", indexes = {
    @Index(name = "idx_stats_doctor", columnList = "doctor_id", unique = true)
})
@RequiredArgsConstructor
@AllArgsConstructor
public class DoctorConsultationStats {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "doctor_id", nullable = false, unique = true)
    private DoctorProfile doctor;

    @Column(name = "average_consultation_minutes")
    @Builder.Default
    private Integer averageConsultationMinutes = 15;

    @Column(name = "total_consultations")
    @Builder.Default
    private Integer totalConsultations = 0;

    @Column(name = "total_consultation_minutes")
    @Builder.Default
    private Long totalConsultationMinutes = 0L;

    @Column(name = "last_updated")
    private Date lastUpdated;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
