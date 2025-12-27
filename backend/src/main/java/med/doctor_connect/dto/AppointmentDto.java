package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import med.doctor_connect.model.AppointmentStatus;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private String id;
    private ScheduleSlotDto slot;
    private DoctorProfileDto doctor;
    private PatientProfileDto patient;
    private AppointmentStatus status;
    private Date appointmentTime;
    private String reason;
    private BigDecimal fee;
    private Date createdAt;
    private Date updatedAt;
}
