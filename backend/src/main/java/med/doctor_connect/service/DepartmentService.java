package med.doctor_connect.service;

import med.doctor_connect.dto.DepartmentDto;

import java.util.List;

public interface DepartmentService {

    List<DepartmentDto> getAllDepartments();

    DepartmentDto createDepartment(DepartmentDto departmentDto);
}
