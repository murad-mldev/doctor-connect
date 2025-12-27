package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    private UUID doctorId;
    private UUID slotId;
    private UUID patientId;
    private String reason;
    private String idempotencyKey;
}
