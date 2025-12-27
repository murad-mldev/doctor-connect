package med.doctor_connect.repository;

import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.PatientProfile;
import med.doctor_connect.model.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    Optional<Prescription> findByAppointment(Appointment appointment);

    Page<Prescription> findByPatient(PatientProfile patient, Pageable pageable);

    Page<Prescription> findByPatientId(UUID patientId, Pageable pageable);
}
