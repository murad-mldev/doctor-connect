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
public class PatientProfileDto {
    private String id;
    private UserDto user;
    private Date dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String address;
    private String emergencyContact;
    private Date createdAt;
    private Date updatedAt;
}
