package med.doctor_connect.repository;

import med.doctor_connect.model.DoctorConsultationStats;
import med.doctor_connect.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorConsultationStatsRepository extends JpaRepository<DoctorConsultationStats, UUID> {

    Optional<DoctorConsultationStats> findByDoctor(DoctorProfile doctor);

    Optional<DoctorConsultationStats> findByDoctorId(UUID doctorId);
}
