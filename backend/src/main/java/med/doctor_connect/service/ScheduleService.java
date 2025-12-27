package med.doctor_connect.service;

import med.doctor_connect.dto.CreateScheduleSlotRequest;
import med.doctor_connect.dto.ScheduleSlotDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleService {

    List<ScheduleSlotDto> getDoctorSchedules(UUID doctorId, LocalDate date);

    /**
     * Get only available (not fully booked) schedule slots for a doctor
     */
    List<ScheduleSlotDto> getAvailableSchedules(UUID doctorId, LocalDate date);

    ScheduleSlotDto createScheduleSlot(UUID doctorId, CreateScheduleSlotRequest request);

    ScheduleSlotDto updateScheduleSlot(UUID doctorId, UUID slotId, CreateScheduleSlotRequest request);

    void deleteScheduleSlot(UUID doctorId, UUID slotId);
}
