package med.doctor_connect.mapper;

import med.doctor_connect.dto.RoleDto;
import med.doctor_connect.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto toDto(Role role);

    Role toEntity(RoleDto dto);
}
