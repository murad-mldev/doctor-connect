package med.doctor_connect.mapper;

import med.doctor_connect.dto.PatientProfileDto;
import med.doctor_connect.model.PatientProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserMapper.class})
public interface PatientProfileMapper {

    PatientProfileMapper INSTANCE = Mappers.getMapper(PatientProfileMapper.class);

    PatientProfileDto toDto(PatientProfile patientProfile);

    PatientProfile toEntity(PatientProfileDto patientProfileDto);
}
