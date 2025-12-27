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
@Table(name = "doctor_profile")
@RequiredArgsConstructor
@AllArgsConstructor
public class DoctorProfile {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "designation")
    private String designation;

    @Column(name = "qualifications")
    private String qualifications;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "approved")
    @Builder.Default
    private boolean approved = false;

    @Column(name = "stripe_connect_account_id")
    private String stripeConnectAccountId;

    @Column(name = "stripe_account_status")
    private String stripeAccountStatus;

    @Column(name = "stripe_onboarding_completed")
    @Builder.Default
    private boolean stripeOnboardingCompleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
