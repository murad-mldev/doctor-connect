package med.doctor_connect.service;

import med.doctor_connect.dto.CreatePrescriptionRequest;
import med.doctor_connect.dto.PrescriptionDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PrescriptionService {

    PrescriptionDto createPrescription(UUID appointmentId, CreatePrescriptionRequest request);

    PrescriptionDto getPrescriptionById(UUID id);

    Page<PrescriptionDto> getPatientPrescriptions(UUID patientId, int page, int limit);
}
