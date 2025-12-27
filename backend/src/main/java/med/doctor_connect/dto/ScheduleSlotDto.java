package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSlotDto {
    private String id;
    private DoctorProfileDto doctor;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private int bookedCount;
    private Date createdAt;
    private Date updatedAt;
}
