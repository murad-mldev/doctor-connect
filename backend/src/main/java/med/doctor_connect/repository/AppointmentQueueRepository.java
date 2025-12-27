package med.doctor_connect.repository;

import med.doctor_connect.model.AppointmentQueue;
import med.doctor_connect.model.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentQueueRepository extends JpaRepository<AppointmentQueue, UUID> {

    Optional<AppointmentQueue> findByDoctorAndQueueDate(DoctorProfile doctor, Date queueDate);

    Optional<AppointmentQueue> findByDoctor(DoctorProfile doctor);
}
