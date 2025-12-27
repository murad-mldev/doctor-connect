package med.doctor_connect.repository;

import med.doctor_connect.model.Prescription;
import med.doctor_connect.model.PrescriptionMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionMedicineRepository extends JpaRepository<PrescriptionMedicine, UUID> {

    List<PrescriptionMedicine> findByPrescription(Prescription prescription);

    List<PrescriptionMedicine> findByPrescriptionId(UUID prescriptionId);
}
