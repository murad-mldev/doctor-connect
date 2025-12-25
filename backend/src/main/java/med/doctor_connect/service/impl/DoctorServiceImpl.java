package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.mapper.DoctorProfileMapper;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.repository.DoctorProfileRepository;
import med.doctor_connect.repository.ScheduleSlotRepository;
import med.doctor_connect.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final DoctorProfileMapper doctorMapper = DoctorProfileMapper.INSTANCE;

    @Override
    public Page<DoctorProfileDto> searchDoctors(UUID departmentId, String specialization, String name, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<DoctorProfile> doctors = doctorProfileRepository.searchDoctors(departmentId, specialization, name, pageable);
        return doctors.map(doctorMapper::toDto);
    }

    @Override
    public DoctorProfileDto getDoctorById(UUID id) {
        DoctorProfile doctor = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return doctorMapper.toDto(doctor);
    }

    @Override
    public boolean hasAvailableSlots(UUID doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return scheduleSlotRepository.hasAvailableSlots(doctor, LocalDate.now());
    }
}
