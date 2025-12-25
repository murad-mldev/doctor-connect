package med.doctor_connect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private String id;

    @NotBlank(message = "Role name cannot be blank")
    private String name;
}
