package med.doctor_connect.mapper;

import med.doctor_connect.dto.UserDto;
import med.doctor_connect.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username", target = "emailOrPhoneNumber")
    UserDto toDto(User user);

    @Mapping(source = "emailOrPhoneNumber", target = "username")
    User toEntity(UserDto dto);
}
