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
public class MedicineDto {
    private String id;
    private String name;
    private String dosageForm;
    private String manufacturer;
    private String description;
    private boolean isActive;
    private Date createdAt;
}
