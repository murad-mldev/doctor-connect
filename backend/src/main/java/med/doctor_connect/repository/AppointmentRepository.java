package med.doctor_connect.repository;

import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.AppointmentStatus;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.model.PatientProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByPatient(PatientProfile patient, Pageable pageable);

    Page<Appointment> findByDoctor(DoctorProfile doctor, Pageable pageable);

    @Query(value = "SELECT DISTINCT a FROM Appointment a " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH p.user " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.department " +
           "LEFT JOIN FETCH a.slot " +
           "WHERE a.patient = :patient AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "a.appointmentTime >= :fromDate AND " +
           "a.appointmentTime <= :toDate",
           countQuery = "SELECT COUNT(DISTINCT a) FROM Appointment a " +
           "WHERE a.patient = :patient AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "a.appointmentTime >= :fromDate AND " +
           "a.appointmentTime <= :toDate")
    Page<Appointment> findPatientAppointments(
        @Param("patient") PatientProfile patient,
        @Param("status") AppointmentStatus status,
        @Param("fromDate") Date fromDate,
        @Param("toDate") Date toDate,
        Pageable pageable
    );

    @Query(value = "SELECT DISTINCT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.department " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH p.user " +
           "LEFT JOIN FETCH a.slot " +
           "WHERE a.doctor = :doctor AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "a.appointmentTime >= :fromDate AND " +
           "a.appointmentTime <= :toDate",
           countQuery = "SELECT COUNT(DISTINCT a) FROM Appointment a " +
           "WHERE a.doctor = :doctor AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "a.appointmentTime >= :fromDate AND " +
           "a.appointmentTime <= :toDate")
    Page<Appointment> findDoctorAppointments(
        @Param("doctor") DoctorProfile doctor,
        @Param("status") AppointmentStatus status,
        @Param("fromDate") Date fromDate,
        @Param("toDate") Date toDate,
        Pageable pageable
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentTime >= :fromDate")
    long countAppointmentsSince(@Param("fromDate") Date fromDate);

    // Queue management methods
    List<Appointment> findByDoctorAndStatus(DoctorProfile doctor, AppointmentStatus status);

    List<Appointment> findByDoctorAndStatusIn(DoctorProfile doctor, List<AppointmentStatus> statuses);
}
