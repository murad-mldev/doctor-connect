package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreatePaymentRequest;
import med.doctor_connect.dto.PaymentDto;
import med.doctor_connect.model.Appointment;
import med.doctor_connect.model.Payment;
import med.doctor_connect.model.PaymentStatus;
import med.doctor_connect.repository.AppointmentRepository;
import med.doctor_connect.repository.PaymentRepository;
import med.doctor_connect.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public PaymentDto createPayment(CreatePaymentRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (paymentRepository.findByAppointment(appointment).isPresent()) {
            throw new RuntimeException("Payment already exists for this appointment");
        }

        Payment payment = Payment.builder()
                .appointment(appointment)
                .patient(appointment.getPatient())
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .transactionId(request.getTransactionId())
                .paymentDetails(request.getPaymentDetails())
                .build();

        Payment saved = paymentRepository.save(payment);
        return buildPaymentDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return buildPaymentDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByAppointmentId(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Payment payment = paymentRepository.findByAppointment(appointment)
                .orElseThrow(() -> new RuntimeException("Payment not found for this appointment"));

        return buildPaymentDto(payment);
    }

    @Override
    public PaymentDto confirmPayment(UUID paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);

        Payment updated = paymentRepository.save(payment);
        return buildPaymentDto(updated);
    }

    @Override
    public PaymentDto updatePaymentStatus(UUID paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(status);

        Payment updated = paymentRepository.save(payment);
        return buildPaymentDto(updated);
    }

    private PaymentDto buildPaymentDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId().toString())
                .appointmentId(payment.getAppointment().getId().toString())
                .patientId(payment.getPatient().getId().toString())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paymentDetails(payment.getPaymentDetails())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
