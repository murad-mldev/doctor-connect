package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.UserDto;
import med.doctor_connect.mapper.UserMapper;
import med.doctor_connect.model.Role;
import med.doctor_connect.model.User;
import med.doctor_connect.repository.RoleRepository;
import med.doctor_connect.repository.UserRepository;
import med.doctor_connect.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getEmailOrPhoneNumber())) {
            throw new RuntimeException("User already exists");
        }

        User user = userMapper.toEntity(userDto);

        if (userDto.getEmailOrPhoneNumber().contains("@")) {
            user.setEmail(userDto.getEmailOrPhoneNumber());
        } else {
            user.setPhone(userDto.getEmailOrPhoneNumber());
        }

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // map role IDs or names to Role entities
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleDto -> {
                        // Try to find by ID first if provided
                        if (roleDto.getId() != null && !roleDto.getId().isBlank()) {
                            return roleRepository.findById(UUID.fromString(roleDto.getId()))
                                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleDto.getId()));
                        }
                        // Otherwise, try to find by name
                        if (roleDto.getName() != null && !roleDto.getName().isBlank()) {
                            return roleRepository.findByName(roleDto.getName())
                                    .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleDto.getName()));
                        }
                        throw new RuntimeException("Role must have either ID or name");
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UUID userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(userDto.getFullName());
        if (userDto.getEmailOrPhoneNumber().contains("@")) {
            user.setEmail(userDto.getEmailOrPhoneNumber());
        } else {
            user.setPhone(userDto.getEmailOrPhoneNumber());
        }
        log.info("User is active: {}", userDto.isActive());
        user.setActive(userDto.isActive());
        user.setVerified(userDto.isVerified());

        // update roles
        if (userDto.getRoles() != null) {
            Set<Role> roles = userDto.getRoles().stream()
                    .map(roleDto -> {
                        // Try to find by ID first if provided
                        if (roleDto.getId() != null && !roleDto.getId().isBlank()) {
                            return roleRepository.findById(UUID.fromString(roleDto.getId()))
                                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleDto.getId()));
                        }
                        // Otherwise, try to find by name
                        if (roleDto.getName() != null && !roleDto.getName().isBlank()) {
                            return roleRepository.findByName(roleDto.getName())
                                    .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleDto.getName()));
                        }
                        throw new RuntimeException("Role must have either ID or name");
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        // update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByEmailOrPhone(String emailOrPhoneNumber) {
        User user = userRepository.findByUsername(emailOrPhoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDto> findUsernameByEmailOrPhone(String emailOrPhoneNumber) {
        return userRepository.findByUsername(emailOrPhoneNumber)
                .map(userMapper::toDto);
    }
}


