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
public class DoctorProfileDto {
    private String id;
    private UserDto user;
    private String description;
    private DepartmentDto department;
    private String specialization;
    private String designation;
    private String qualifications;
    private String licenseNumber;
    private boolean approved;
    private String stripeConnectAccountId;
    private String stripeAccountStatus;
    private boolean stripeOnboardingCompleted;
    private Date createdAt;
    private Date updatedAt;
}
