package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleSlotRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
}
