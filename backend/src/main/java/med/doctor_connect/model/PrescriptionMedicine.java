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
@Table(name = "prescription_medicine", indexes = {
    @Index(name = "idx_pres_med_prescription", columnList = "prescription_id")
})
@RequiredArgsConstructor
@AllArgsConstructor
public class PrescriptionMedicine {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "duration")
    private String duration;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
}
