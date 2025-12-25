package med.doctor_connect.service;

import med.doctor_connect.dto.DoctorProfileDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DoctorService {

    Page<DoctorProfileDto> searchDoctors(UUID departmentId, String specialization, String name, int page, int limit);

    DoctorProfileDto getDoctorById(UUID id);

    boolean hasAvailableSlots(UUID doctorId);
}
