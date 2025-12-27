package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDto {
    private long totalDoctors;
    private long activeDoctors;
    private long pendingDoctorVerifications;
    private long totalPatients;
    private long totalAppointments;
    private long appointmentsToday;
    private long appointmentsThisWeek;
    private long appointmentsThisMonth;
}
