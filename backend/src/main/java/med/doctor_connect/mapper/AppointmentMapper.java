package med.doctor_connect.mapper;

import med.doctor_connect.dto.AppointmentDto;
import med.doctor_connect.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ScheduleSlotMapper.class, DoctorProfileMapper.class, PatientProfileMapper.class})
public interface AppointmentMapper {

    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    AppointmentDto toDto(Appointment appointment);

    Appointment toEntity(AppointmentDto appointmentDto);
}
