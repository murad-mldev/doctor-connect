package med.doctor_connect.service;

import med.doctor_connect.dto.CreatePaymentRequest;
import med.doctor_connect.dto.PaymentDto;
import med.doctor_connect.model.PaymentStatus;

import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(CreatePaymentRequest request);

    PaymentDto getPaymentById(UUID id);

    PaymentDto getPaymentByAppointmentId(UUID appointmentId);

    PaymentDto confirmPayment(UUID paymentId, String transactionId);

    PaymentDto updatePaymentStatus(UUID paymentId, PaymentStatus status);
}
