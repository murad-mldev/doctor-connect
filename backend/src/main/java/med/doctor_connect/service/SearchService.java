package med.doctor_connect.service;

import med.doctor_connect.dto.DoctorProfileDto;
import org.springframework.data.domain.Page;

public interface SearchService {

    Page<DoctorProfileDto> searchDoctors(String query, int page, int limit);
}
