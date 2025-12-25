package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.RoleDto;
import med.doctor_connect.mapper.RoleMapper;
import med.doctor_connect.model.Role;
import med.doctor_connect.repository.RoleRepository;
import med.doctor_connect.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper = RoleMapper.INSTANCE;

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        Role role = roleMapper.toEntity(roleDto);
        Role saved = roleRepository.save(role);
        return roleMapper.toDto(saved);
    }

    @Override
    public RoleDto updateRole(UUID roleId, RoleDto roleDto) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        role.setName(roleDto.getName());
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public void deleteRole(UUID roleId) {
        roleRepository.deleteById(roleId);
    }

    @Override
    public RoleDto getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        return roleMapper.toDto(role);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toDto).collect(Collectors.toList());
    }
}

