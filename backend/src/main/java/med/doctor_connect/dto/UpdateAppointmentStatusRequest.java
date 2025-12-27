package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import med.doctor_connect.model.AppointmentStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentStatusRequest {
    private AppointmentStatus status;
}
