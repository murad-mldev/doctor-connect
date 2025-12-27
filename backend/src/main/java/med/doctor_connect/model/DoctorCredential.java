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
@Table(name = "doctor_credential", indexes = {
    @Index(name = "idx_doctor_cred_doctor", columnList = "doctor_id")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class DoctorCredential {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private FileStore file;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Date uploadedAt;

    @Column(name = "verified")
    @Builder.Default
    private boolean verified = false;

    @Column(name = "verified_at")
    private Date verifiedAt;
}
