package med.doctor_connect.repository;

import med.doctor_connect.model.PatientProfile;
import med.doctor_connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, UUID> {

    Optional<PatientProfile> findByUser(User user);

    Optional<PatientProfile> findByUserId(UUID userId);
}
