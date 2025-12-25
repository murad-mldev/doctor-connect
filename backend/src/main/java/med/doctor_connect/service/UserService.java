package med.doctor_connect.service;

import med.doctor_connect.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UUID userId, UserDto userDto);

    void deleteUser(UUID userId);

    UserDto getUserById(UUID userId);

    List<UserDto> getAllUsers();

    UserDto getUserByEmailOrPhone(String emailOrPhoneNumber);

    Optional<UserDto> findUsernameByEmailOrPhone(String emailOrPhoneNumber);
}

