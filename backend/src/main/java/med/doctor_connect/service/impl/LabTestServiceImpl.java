package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.LabTestDto;
import med.doctor_connect.model.LabTest;
import med.doctor_connect.repository.LabTestRepository;
import med.doctor_connect.service.LabTestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class LabTestServiceImpl implements LabTestService {

    private final LabTestRepository labTestRepository;

    @Override
    public LabTestDto createLabTest(LabTestDto labTestDto) {
        LabTest labTest = LabTest.builder()
                .name(labTestDto.getName())
                .description(labTestDto.getDescription())
                .isActive(true)
                .build();

        LabTest saved = labTestRepository.save(labTest);
        return buildLabTestDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LabTestDto getLabTestById(UUID id) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab test not found"));
        return buildLabTestDto(labTest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LabTestDto> getAllActiveLabTests() {
        return labTestRepository.findByIsActiveTrue()
                .stream()
                .map(this::buildLabTestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabTestDto> searchLabTests(String name, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<LabTest> labTests = labTestRepository.findByNameContainingIgnoreCase(name, pageable);
        return labTests.map(this::buildLabTestDto);
    }

    @Override
    public LabTestDto updateLabTest(UUID id, LabTestDto labTestDto) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab test not found"));

        if (labTestDto.getName() != null) {
            labTest.setName(labTestDto.getName());
        }
        if (labTestDto.getDescription() != null) {
            labTest.setDescription(labTestDto.getDescription());
        }

        LabTest updated = labTestRepository.save(labTest);
        return buildLabTestDto(updated);
    }

    @Override
    public void deleteLabTest(UUID id) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab test not found"));
        labTest.setActive(false);
        labTestRepository.save(labTest);
    }

    private LabTestDto buildLabTestDto(LabTest labTest) {
        return LabTestDto.builder()
                .id(labTest.getId().toString())
                .name(labTest.getName())
                .description(labTest.getDescription())
                .isActive(labTest.isActive())
                .createdAt(labTest.getCreatedAt())
                .build();
    }
}
