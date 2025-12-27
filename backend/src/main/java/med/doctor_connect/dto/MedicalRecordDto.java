package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDto {
    private String id;
    private String patientId;
    private String doctorId;
    private String title;
    private String diagnosis;
    private String treatment;
    private String attachments;
    private LocalDate recordDate;
    private Date createdAt;
}
