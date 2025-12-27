package med.doctor_connect.repository;

import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoRoomRepository extends JpaRepository<VideoRoom, UUID> {

    Optional<VideoRoom> findByAppointment(Appointment appointment);

    Optional<VideoRoom> findByMeetingCode(String meetingCode);
}
