package med.doctor_connect.mapper;

import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.model.DoctorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class, DepartmentMapper.class})
public interface DoctorProfileMapper {
    DoctorProfileMapper INSTANCE = Mappers.getMapper(DoctorProfileMapper.class);
    DoctorProfileDto toDto(DoctorProfile doctorProfile);

    DoctorProfile toEntity(DoctorProfileDto doctorProfileDto);
}
