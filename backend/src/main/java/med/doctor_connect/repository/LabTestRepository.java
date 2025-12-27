package med.doctor_connect.repository;

import med.doctor_connect.model.LabTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, UUID> {

    List<LabTest> findByIsActiveTrue();

    Page<LabTest> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
