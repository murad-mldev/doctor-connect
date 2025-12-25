package med.doctor_connect.service;

import med.doctor_connect.dto.AdminStatsDto;
import med.doctor_connect.dto.DoctorProfileDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminService {

    Page<DoctorProfileDto> getPendingDoctorVerifications(int page, int limit);

    DoctorProfileDto verifyDoctor(UUID doctorId);

    DoctorProfileDto rejectDoctor(UUID doctorId);

    AdminStatsDto getSystemStats();
}
