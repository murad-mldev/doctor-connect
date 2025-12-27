package med.doctor_connect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Builder
@Data
@Entity
@Table(name = "prescription_test", indexes = {
    @Index(name = "idx_pres_test_prescription", columnList = "prescription_id")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class PrescriptionTest {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private LabTest test;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
}
