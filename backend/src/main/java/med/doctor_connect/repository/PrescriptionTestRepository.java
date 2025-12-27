package med.doctor_connect.repository;

import med.doctor_connect.model.Prescription;
import med.doctor_connect.model.PrescriptionTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionTestRepository extends JpaRepository<PrescriptionTest, UUID> {

    List<PrescriptionTest> findByPrescription(Prescription prescription);

    List<PrescriptionTest> findByPrescriptionId(UUID prescriptionId);
}
