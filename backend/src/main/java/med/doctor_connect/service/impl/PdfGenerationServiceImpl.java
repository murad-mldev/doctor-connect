package med.doctor_connect.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import med.doctor_connect.model.Prescription;
import med.doctor_connect.model.PrescriptionMedicine;
import med.doctor_connect.model.PrescriptionTest;
import med.doctor_connect.repository.PrescriptionMedicineRepository;
import med.doctor_connect.repository.PrescriptionTestRepository;
import med.doctor_connect.service.PdfGenerationService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private final PrescriptionMedicineRepository medicineRepository;
    private final PrescriptionTestRepository testRepository;

    @Override
    public ByteArrayOutputStream generatePrescriptionPdf(Prescription prescription) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            document.add(new Paragraph("PRESCRIPTION")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // Doctor Information
            document.add(new Paragraph("Doctor Information")
                    .setFontSize(14)
                    .setBold());
            document.add(new Paragraph("Name: " + prescription.getDoctor().getUser().getFullName()));
            document.add(new Paragraph("Specialization: " + prescription.getDoctor().getSpecialization()));
            document.add(new Paragraph("Department: " + (prescription.getDoctor().getDepartment() != null ?
                    prescription.getDoctor().getDepartment().getName() : "N/A")));
            document.add(new Paragraph("License: " + prescription.getDoctor().getLicenseNumber()));

            document.add(new Paragraph("\n"));

            // Patient Information
            document.add(new Paragraph("Patient Information")
                    .setFontSize(14)
                    .setBold());
            document.add(new Paragraph("Name: " + prescription.getPatient().getUser().getFullName()));
            document.add(new Paragraph("Phone: " + prescription.getPatient().getUser().getPhone()));
            document.add(new Paragraph("Blood Group: " + (prescription.getPatient().getBloodGroup() != null ?
                    prescription.getPatient().getBloodGroup() : "N/A")));

            document.add(new Paragraph("\n"));

            // Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            document.add(new Paragraph("Date: " + sdf.format(prescription.getCreatedAt())));

            document.add(new Paragraph("\n"));

            // Medicines
            List<PrescriptionMedicine> medicines = medicineRepository.findByPrescription(prescription);
            if (!medicines.isEmpty()) {
                document.add(new Paragraph("Medicines")
                        .setFontSize(14)
                        .setBold());

                Table medicineTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 3}));
                medicineTable.setWidth(UnitValue.createPercentValue(100));

                // Table headers
                medicineTable.addHeaderCell("Medicine Name");
                medicineTable.addHeaderCell("Dosage");
                medicineTable.addHeaderCell("Duration");
                medicineTable.addHeaderCell("Instructions");

                for (PrescriptionMedicine med : medicines) {
                    medicineTable.addCell(med.getName());
                    medicineTable.addCell(med.getDosage() != null ? med.getDosage() : "");
                    medicineTable.addCell(med.getDuration() != null ? med.getDuration() : "");
                    medicineTable.addCell(med.getInstructions() != null ? med.getInstructions() : "");
                }

                document.add(medicineTable);
                document.add(new Paragraph("\n"));
            }

            // Lab Tests
            List<PrescriptionTest> tests = testRepository.findByPrescription(prescription);
            if (!tests.isEmpty()) {
                document.add(new Paragraph("Recommended Tests")
                        .setFontSize(14)
                        .setBold());

                Table testTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
                testTable.setWidth(UnitValue.createPercentValue(100));

                testTable.addHeaderCell("Test Name");
                testTable.addHeaderCell("Instructions");

                for (PrescriptionTest test : tests) {
                    testTable.addCell(test.getName());
                    testTable.addCell(test.getInstructions() != null ? test.getInstructions() : "");
                }

                document.add(testTable);
                document.add(new Paragraph("\n"));
            }

            // Notes
            if (prescription.getNotes() != null && !prescription.getNotes().isEmpty()) {
                document.add(new Paragraph("Additional Notes")
                        .setFontSize(14)
                        .setBold());
                document.add(new Paragraph(prescription.getNotes()));
            }

            // Footer
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Doctor's Signature: _____________________")
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }

        return baos;
    }
}
