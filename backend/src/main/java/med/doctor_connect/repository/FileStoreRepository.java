package med.doctor_connect.repository;

import med.doctor_connect.model.FileStore;
import med.doctor_connect.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileStoreRepository extends JpaRepository<FileStore, UUID> {

    List<FileStore> findByOwnerUser(User ownerUser);

    Page<FileStore> findByOwnerUserId(UUID ownerUserId, Pageable pageable);

    List<FileStore> findByRelatedEntityTypeAndRelatedEntityId(String entityType, UUID entityId);
}
