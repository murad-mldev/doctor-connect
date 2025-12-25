package med.doctor_connect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;

    @NotBlank(message = "Email or phone number cannot be blank")
    @Size(max = 255, message = "Value too long")
    private String emailOrPhoneNumber;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 255)
    private String fullName;

    @Size(max = 50)
    private String phone;

    @Email
    private String email;

    private boolean isActive;
    private boolean isVerified;

    private Set<RoleDto> roles;
}
