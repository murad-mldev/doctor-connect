package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {
    private String id;
    private AppointmentDto appointment;
    private DoctorProfileDto doctor;
    private PatientProfileDto patient;
    private String notes;
    private List<PrescriptionMedicineDto> medicines;
    private List<PrescriptionTestDto> tests;
    private String pdfFileId;
    private Date createdAt;
    private Date updatedAt;
}
