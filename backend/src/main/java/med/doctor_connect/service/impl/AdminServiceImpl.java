package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.AdminStatsDto;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.mapper.DoctorProfileMapper;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.repository.*;
import med.doctor_connect.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorProfileMapper doctorMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorProfileDto> getPendingDoctorVerifications(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<DoctorProfile> doctors = doctorProfileRepository.findByApprovedFalse(pageable);
        return doctors.map(doctorMapper::toDto);
    }

    @Override
    public DoctorProfileDto verifyDoctor(UUID doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (doctor.isApproved()) {
            throw new RuntimeException("Doctor already verified");
        }

        // Update doctor profile
        doctor.setApproved(true);

        // Also verify the user account
        doctor.getUser().setVerified(true);

        DoctorProfile updated = doctorProfileRepository.save(doctor);
        return doctorMapper.toDto(updated);
    }

    @Override
    public DoctorProfileDto rejectDoctor(UUID doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Update doctor profile
        doctor.setApproved(false);

        // Also unverify the user account if rejecting
        doctor.getUser().setVerified(false);

        DoctorProfile updated = doctorProfileRepository.save(doctor);
        return doctorMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsDto getSystemStats() {
        long totalDoctors = doctorProfileRepository.count();
        long activeDoctors = doctorProfileRepository.findByApprovedTrue(PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
        long pendingDoctorVerifications = doctorProfileRepository.countByApprovedFalse();
        long totalPatients = patientProfileRepository.count();
        long totalAppointments = appointmentRepository.count();

        // Today's appointments
        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        long appointmentsToday = appointmentRepository.countAppointmentsSince(startOfDay);

        // This week's appointments
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        Date startOfWeek = cal.getTime();
        long appointmentsThisWeek = appointmentRepository.countAppointmentsSince(startOfWeek);

        // This month's appointments
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = cal.getTime();
        long appointmentsThisMonth = appointmentRepository.countAppointmentsSince(startOfMonth);

        return AdminStatsDto.builder()
                .totalDoctors(totalDoctors)
                .activeDoctors(activeDoctors)
                .pendingDoctorVerifications(pendingDoctorVerifications)
                .totalPatients(totalPatients)
                .totalAppointments(totalAppointments)
                .appointmentsToday(appointmentsToday)
                .appointmentsThisWeek(appointmentsThisWeek)
                .appointmentsThisMonth(appointmentsThisMonth)
                .build();
    }
}
