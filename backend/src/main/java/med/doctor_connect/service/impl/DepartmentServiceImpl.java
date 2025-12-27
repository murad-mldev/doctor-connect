package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.DepartmentDto;
import med.doctor_connect.mapper.DepartmentMapper;
import med.doctor_connect.model.Department;
import med.doctor_connect.repository.DepartmentRepository;
import med.doctor_connect.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(departmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        if (departmentRepository.existsByName(departmentDto.getName())) {
            throw new RuntimeException("Department already exists");
        }

        Department department = Department.builder()
                .name(departmentDto.getName())
                .description(departmentDto.getDescription())
                .build();

        Department saved = departmentRepository.save(department);
        return departmentMapper.toDto(saved);
    }
}
