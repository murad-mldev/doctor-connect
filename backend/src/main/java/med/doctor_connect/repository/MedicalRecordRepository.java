package med.doctor_connect.repository;

import med.doctor_connect.model.MedicalRecord;
import med.doctor_connect.model.PatientProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    Page<MedicalRecord> findByPatient(PatientProfile patient, Pageable pageable);

    Page<MedicalRecord> findByPatientId(UUID patientId, Pageable pageable);
}
