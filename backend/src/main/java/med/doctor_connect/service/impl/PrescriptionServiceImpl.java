package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreatePrescriptionRequest;
import med.doctor_connect.dto.PrescriptionDto;
import med.doctor_connect.dto.PrescriptionMedicineDto;
import med.doctor_connect.dto.PrescriptionTestDto;
import med.doctor_connect.model.*;
import med.doctor_connect.repository.*;
import med.doctor_connect.service.PdfGenerationService;
import med.doctor_connect.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMedicineRepository prescriptionMedicineRepository;
    private final PrescriptionTestRepository prescriptionTestRepository;
    private final AppointmentRepository appointmentRepository;
    private final FileStoreRepository fileStoreRepository;
    private final PdfGenerationService pdfGenerationService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public PrescriptionDto createPrescription(UUID appointmentId, CreatePrescriptionRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Check if prescription already exists
        if (prescriptionRepository.findByAppointment(appointment).isPresent()) {
            throw new RuntimeException("Prescription already exists for this appointment");
        }

        // Create prescription
        Prescription prescription = Prescription.builder()
                .appointment(appointment)
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .notes(request.getNotes())
                .build();

        Prescription saved = prescriptionRepository.save(prescription);

        // Create medicines
        List<PrescriptionMedicine> medicines = new ArrayList<>();
        if (request.getMedicines() != null) {
            for (PrescriptionMedicineDto medDto : request.getMedicines()) {
                PrescriptionMedicine medicine = PrescriptionMedicine.builder()
                        .prescription(saved)
                        .name(medDto.getName())
                        .dosage(medDto.getDosage())
                        .duration(medDto.getDuration())
                        .instructions(medDto.getInstructions())
                        .build();
                medicines.add(prescriptionMedicineRepository.save(medicine));
            }
        }

        // Create tests
        List<PrescriptionTest> tests = new ArrayList<>();
        if (request.getTests() != null) {
            for (PrescriptionTestDto testDto : request.getTests()) {
                PrescriptionTest test = PrescriptionTest.builder()
                        .prescription(saved)
                        .name(testDto.getName())
                        .instructions(testDto.getInstructions())
                        .build();
                tests.add(prescriptionTestRepository.save(test));
            }
        }

        // Generate PDF
        try {
            ByteArrayOutputStream pdfOutput = pdfGenerationService.generatePrescriptionPdf(saved);

            // Save PDF file
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = "prescription_" + saved.getId() + ".pdf";
            Path filePath = uploadPath.resolve(fileName);

            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                pdfOutput.writeTo(fos);
            }

            // Save file metadata
            FileStore fileStore = FileStore.builder()
                    .ownerUser(saved.getPatient().getUser())
                    .relatedEntityType("PRESCRIPTION")
                    .relatedEntityId(saved.getId())
                    .fileKey(fileName)
                    .fileName(fileName)
                    .fileType(FileTypeEnum.PRESCRIPTION_PDF)
                    .sizeBytes((long) pdfOutput.size())
                    .uploadedBy(saved.getDoctor().getUser())
                    .build();

            FileStore savedFile = fileStoreRepository.save(fileStore);

            // Link to prescription
            saved.setPdfFile(savedFile);
            prescriptionRepository.save(saved);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save prescription PDF: " + e.getMessage());
        }

        return buildPrescriptionDto(saved, medicines, tests);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionDto getPrescriptionById(UUID id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        List<PrescriptionMedicine> medicines = prescriptionMedicineRepository.findByPrescription(prescription);
        List<PrescriptionTest> tests = prescriptionTestRepository.findByPrescription(prescription);

        return buildPrescriptionDto(prescription, medicines, tests);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PrescriptionDto> getPatientPrescriptions(UUID patientId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Prescription> prescriptions = prescriptionRepository.findByPatientId(patientId, pageable);

        return prescriptions.map(prescription -> {
            List<PrescriptionMedicine> medicines = prescriptionMedicineRepository.findByPrescription(prescription);
            List<PrescriptionTest> tests = prescriptionTestRepository.findByPrescription(prescription);
            return buildPrescriptionDto(prescription, medicines, tests);
        });
    }

    private PrescriptionDto buildPrescriptionDto(Prescription prescription, List<PrescriptionMedicine> medicines, List<PrescriptionTest> tests) {
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
                .pdfFileId(prescription.getPdfFile() != null ? prescription.getPdfFile().getId().toString() : null)
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }
}
