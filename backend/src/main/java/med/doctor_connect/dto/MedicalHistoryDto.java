package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryDto {
    private PatientProfileDto patient;
    private List<AppointmentDto> appointments;
    private List<PrescriptionDto> prescriptions;
    private List<MedicalRecordDto> medicalRecords;
}
