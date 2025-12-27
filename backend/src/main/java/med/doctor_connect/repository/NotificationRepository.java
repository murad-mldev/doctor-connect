package med.doctor_connect.repository;

import med.doctor_connect.model.Notification;
import med.doctor_connect.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUser(User user, Pageable pageable);

    Page<Notification> findByUserAndIsReadFalse(User user, Pageable pageable);

    long countByUserAndIsReadFalse(User user);
}
