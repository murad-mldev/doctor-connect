package med.doctor_connect.repository;

import med.doctor_connect.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, UUID> {

    List<Medicine> findByIsActiveTrue();

    Page<Medicine> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
