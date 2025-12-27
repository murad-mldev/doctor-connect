package med.doctor_connect.repository;

import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.model.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, UUID> {

    List<ScheduleSlot> findByDoctorAndDate(DoctorProfile doctor, LocalDate date);

    List<ScheduleSlot> findByDoctorIdAndDate(UUID doctorId, LocalDate date);

    @Query("SELECT s FROM ScheduleSlot s WHERE s.doctor = :doctor AND " +
           "s.date = :date AND s.startTime = :startTime AND s.endTime = :endTime")
    Optional<ScheduleSlot> findConflictingSlot(
        @Param("doctor") DoctorProfile doctor,
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ScheduleSlot s " +
           "WHERE s.doctor = :doctor AND s.date >= :date AND s.bookedCount < s.capacity")
    boolean hasAvailableSlots(@Param("doctor") DoctorProfile doctor, @Param("date") LocalDate date);
}
