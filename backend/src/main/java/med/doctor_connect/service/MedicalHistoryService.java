package med.doctor_connect.service;

import med.doctor_connect.dto.MedicalHistoryDto;

import java.util.UUID;

public interface MedicalHistoryService {

    MedicalHistoryDto getPatientMedicalHistory(UUID patientId);
}
