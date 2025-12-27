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
@Table(name = "medicine", indexes = {
    @Index(name = "idx_medicine_name", columnList = "name")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "dosage_form")
    private String dosageForm;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
}
