package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.*;
import med.doctor_connect.mapper.AppointmentMapper;
import med.doctor_connect.mapper.PatientProfileMapper;
import med.doctor_connect.model.*;
import med.doctor_connect.repository.*;
import med.doctor_connect.service.MedicalHistoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final PatientProfileRepository patientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionMedicineRepository medicineRepository;
    private final PrescriptionTestRepository testRepository;
    private final PatientProfileMapper patientMapper;
    private final AppointmentMapper appointmentMapper;

    @Override
    public MedicalHistoryDto getPatientMedicalHistory(UUID patientId) {
        PatientProfile patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Get appointments
        Pageable pageable = PageRequest.of(0, 100);
        List<AppointmentDto> appointments = appointmentRepository.findPatientAppointments(patient, null, null, null, pageable)
                .stream()
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());

        // Get prescriptions
        List<PrescriptionDto> prescriptions = prescriptionRepository.findByPatientId(patientId, pageable)
                .stream()
                .map(prescription -> {
                    List<PrescriptionMedicine> medicines = medicineRepository.findByPrescription(prescription);
                    List<PrescriptionTest> tests = testRepository.findByPrescription(prescription);

                    List<PrescriptionMedicineDto> medicineDtos = medicines.stream()
                            .map(med -> PrescriptionMedicineDto.builder()
                                    .id(med.getId().toString())
                                    .name(med.getName())
                                    .dosage(med.getDosage())
                                    .duration(med.getDuration())
                                    .instructions(med.getInstructions())
                                    .build())
                            .collect(Collectors.toList());

                    List<PrescriptionTestDto> testDtos = tests.stream()
                            .map(test -> PrescriptionTestDto.builder()
                                    .id(test.getId().toString())
                                    .name(test.getName())
                                    .instructions(test.getInstructions())
                                    .build())
                            .collect(Collectors.toList());

                    return PrescriptionDto.builder()
                            .id(prescription.getId().toString())
                            .notes(prescription.getNotes())
                            .medicines(medicineDtos)
                            .tests(testDtos)
                            .createdAt(prescription.getCreatedAt())
                            .updatedAt(prescription.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        // Get medical records
        List<MedicalRecordDto> medicalRecords = medicalRecordRepository.findByPatientId(patientId, pageable)
                .stream()
                .map(record -> MedicalRecordDto.builder()
                        .id(record.getId().toString())
                        .patientId(record.getPatient().getId().toString())
                        .doctorId(record.getDoctor() != null ? record.getDoctor().getId().toString() : null)
                        .title(record.getTitle())
                        .diagnosis(record.getDiagnosis())
                        .treatment(record.getTreatment())
                        .attachments(record.getAttachments())
                        .recordDate(record.getRecordDate())
                        .createdAt(record.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return MedicalHistoryDto.builder()
                .patient(patientMapper.toDto(patient))
                .appointments(appointments)
                .prescriptions(prescriptions)
                .medicalRecords(medicalRecords)
                .build();
    }
}
