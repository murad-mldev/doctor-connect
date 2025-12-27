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
public class CreatePrescriptionRequest {
    private String notes;
    private List<PrescriptionMedicineDto> medicines;
    private List<PrescriptionTestDto> tests;
    private String idempotencyKey;
}
