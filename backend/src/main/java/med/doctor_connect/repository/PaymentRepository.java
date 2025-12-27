package med.doctor_connect.repository;

import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.Payment;
import med.doctor_connect.model.PatientProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByAppointment(Appointment appointment);

    Page<Payment> findByPatient(PatientProfile patient, Pageable pageable);

    Optional<Payment> findByTransactionId(String transactionId);
}
