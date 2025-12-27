package med.doctor_connect.service;

import med.doctor_connect.dto.LabTestDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface LabTestService {

    LabTestDto createLabTest(LabTestDto labTestDto);

    LabTestDto getLabTestById(UUID id);

    List<LabTestDto> getAllActiveLabTests();

    Page<LabTestDto> searchLabTests(String name, int page, int limit);

    LabTestDto updateLabTest(UUID id, LabTestDto labTestDto);

    void deleteLabTest(UUID id);
}
