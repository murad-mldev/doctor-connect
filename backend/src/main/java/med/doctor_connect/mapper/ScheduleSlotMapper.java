package med.doctor_connect.mapper;

import med.doctor_connect.dto.ScheduleSlotDto;
import med.doctor_connect.model.ScheduleSlot;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {DoctorProfileMapper.class})
public interface ScheduleSlotMapper {

    ScheduleSlotMapper INSTANCE = Mappers.getMapper(ScheduleSlotMapper.class);

    ScheduleSlotDto toDto(ScheduleSlot scheduleSlot);

    ScheduleSlot toEntity(ScheduleSlotDto scheduleSlotDto);
}
