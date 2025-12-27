package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.mapper.DoctorProfileMapper;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.repository.DoctorProfileRepository;
import med.doctor_connect.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorProfileMapper doctorMapper;

    @Override
    public Page<DoctorProfileDto> searchDoctors(String query, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        // Search by name or specialization
        Page<DoctorProfile> doctors = doctorProfileRepository.searchDoctors(null, query, query, pageable);
        return doctors.map(doctorMapper::toDto);
    }
}
