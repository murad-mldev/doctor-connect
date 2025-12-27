package med.doctor_connect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.RoleDto;
import med.doctor_connect.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        RoleDto createdRole = roleService.createRole(roleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable String id,
            @Valid @RequestBody RoleDto roleDto) {
        UUID roleId = UUID.fromString(id);
        RoleDto updatedRole = roleService.updateRole(roleId, roleDto);
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        UUID roleId = UUID.fromString(id);
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable String id) {
        UUID roleId = UUID.fromString(id);
        RoleDto role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}
