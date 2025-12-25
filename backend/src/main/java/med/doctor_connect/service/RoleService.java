package med.doctor_connect.service;

import med.doctor_connect.dto.RoleDto;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleDto createRole(RoleDto roleDto);

    RoleDto updateRole(UUID roleId, RoleDto roleDto);

    void deleteRole(UUID roleId);

    RoleDto getRoleById(UUID roleId);

    List<RoleDto> getAllRoles();
}

