package med.doctor_connect.service;

import med.doctor_connect.dto.AppointmentDto;
import med.doctor_connect.dto.CreateAppointmentRequest;
import med.doctor_connect.dto.UpdateAppointmentStatusRequest;
import med.doctor_connect.model.AppointmentStatus;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.UUID;

public interface AppointmentService {

    AppointmentDto createAppointment(CreateAppointmentRequest request);

    AppointmentDto getAppointmentById(UUID id);

    Page<AppointmentDto> getUserAppointments(UUID userId, AppointmentStatus status, Date fromDate, Date toDate, int page, int limit);

    AppointmentDto updateAppointmentStatus(UUID appointmentId, UpdateAppointmentStatusRequest request);

    AppointmentDto cancelAppointment(UUID appointmentId, String cancellationReason);

    AppointmentDto rescheduleAppointment(UUID appointmentId, UUID newSlotId);
}
