package med.doctor_connect.repository;

import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, UUID> {

    Optional<DoctorProfile> findByUser(User user);

    Optional<DoctorProfile> findByUserId(UUID userId);

    Page<DoctorProfile> findByApprovedTrue(Pageable pageable);

    @Query("SELECT d FROM DoctorProfile d WHERE " +
           "(:departmentId IS NULL OR d.department.id = :departmentId) AND " +
           "(:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:name IS NULL OR LOWER(d.user.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "d.approved = true")
    Page<DoctorProfile> searchDoctors(
        @Param("departmentId") UUID departmentId,
        @Param("specialization") String specialization,
        @Param("name") String name,
        Pageable pageable
    );

    long countByApprovedFalse();

    Page<DoctorProfile> findByApprovedFalse(Pageable pageable);
}
