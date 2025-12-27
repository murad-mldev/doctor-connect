package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionMedicineDto {
    private String id;
    private String name;
    private String dosage;
    private String duration;
    private String instructions;
}
