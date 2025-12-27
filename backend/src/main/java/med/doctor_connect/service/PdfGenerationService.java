package med.doctor_connect.service;

import med.doctor_connect.model.Prescription;

import java.io.ByteArrayOutputStream;

public interface PdfGenerationService {

    ByteArrayOutputStream generatePrescriptionPdf(Prescription prescription);
}
