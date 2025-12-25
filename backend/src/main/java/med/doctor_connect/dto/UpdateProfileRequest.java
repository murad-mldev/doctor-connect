package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String email;

    private Date dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String emergencyContact;

    private String description;
    private String specialization;
    private String designation;
    private String qualifications;
}
