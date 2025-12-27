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
@Table(name = "prescription")
@RequiredArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToOne
    @JoinColumn(name = "pdf_file_id")
    private FileStore pdfFile;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
