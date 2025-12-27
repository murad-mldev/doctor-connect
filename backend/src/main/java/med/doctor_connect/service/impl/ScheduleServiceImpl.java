package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreateScheduleSlotRequest;
import med.doctor_connect.dto.ScheduleSlotDto;
import med.doctor_connect.mapper.ScheduleSlotMapper;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.model.ScheduleSlot;
import med.doctor_connect.repository.DoctorProfileRepository;
import med.doctor_connect.repository.ScheduleSlotRepository;
import med.doctor_connect.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleSlotRepository scheduleSlotRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final ScheduleSlotMapper scheduleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleSlotDto> getDoctorSchedules(UUID doctorId, LocalDate date) {
        List<ScheduleSlot> slots = scheduleSlotRepository.findByDoctorIdAndDate(doctorId, date);
        return slots.stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleSlotDto> getAvailableSchedules(UUID doctorId, LocalDate date) {
        List<ScheduleSlot> slots = scheduleSlotRepository.findByDoctorIdAndDate(doctorId, date);
        // Filter to only return slots that are not fully booked
        return slots.stream()
                .filter(slot -> slot.getBookedCount() < slot.getCapacity())
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleSlotDto createScheduleSlot(UUID doctorId, CreateScheduleSlotRequest request) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check for conflicting slots
        if (scheduleSlotRepository.findConflictingSlot(doctor, request.getDate(), request.getStartTime(), request.getEndTime()).isPresent()) {
            throw new RuntimeException("Schedule conflict: Slot already exists for this time");
        }

        // Validate time range
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().equals(request.getEndTime())) {
            throw new RuntimeException("Invalid time range: Start time must be before end time");
        }

        ScheduleSlot slot = ScheduleSlot.builder()
                .doctor(doctor)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .capacity(request.getCapacity())
                .bookedCount(0)
                .build();

        ScheduleSlot saved = scheduleSlotRepository.save(slot);
        return scheduleMapper.toDto(saved);
    }

    @Override
    public ScheduleSlotDto updateScheduleSlot(UUID doctorId, UUID slotId, CreateScheduleSlotRequest request) {
        ScheduleSlot slot = scheduleSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Schedule slot not found"));

        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized: Slot does not belong to this doctor");
        }

        // Validate time range
        if (request.getStartTime().isAfter(request.getEndTime()) || request.getStartTime().equals(request.getEndTime())) {
            throw new RuntimeException("Invalid time range: Start time must be before end time");
        }

        // Update fields
        if (request.getDate() != null) {
            slot.setDate(request.getDate());
        }
        if (request.getStartTime() != null) {
            slot.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            slot.setEndTime(request.getEndTime());
        }
        if (request.getCapacity() > 0) {
            if (request.getCapacity() < slot.getBookedCount()) {
                throw new RuntimeException("Cannot reduce capacity below booked count");
            }
            slot.setCapacity(request.getCapacity());
        }

        ScheduleSlot updated = scheduleSlotRepository.save(slot);
        return scheduleMapper.toDto(updated);
    }

    @Override
    public void deleteScheduleSlot(UUID doctorId, UUID slotId) {
        ScheduleSlot slot = scheduleSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Schedule slot not found"));

        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Unauthorized: Slot does not belong to this doctor");
        }

        if (slot.getBookedCount() > 0) {
            throw new RuntimeException("Cannot delete slot with existing appointments");
        }

        scheduleSlotRepository.delete(slot);
    }
}
