package med.doctor_connect.mapper;

import med.doctor_connect.dto.DepartmentDto;
import med.doctor_connect.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    DepartmentDto toDto(Department department);

    Department toEntity(DepartmentDto departmentDto);
}
