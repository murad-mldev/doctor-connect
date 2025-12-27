package med.doctor_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "medical_record", indexes = {
    @Index(name = "idx_medical_record_patient", columnList = "patient_id")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor;

    @Column(name = "title")
    private String title;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attachments", columnDefinition = "jsonb")
    private String attachments;

    @Column(name = "record_date", nullable = false)
    @Builder.Default
    private LocalDate recordDate = LocalDate.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
}
