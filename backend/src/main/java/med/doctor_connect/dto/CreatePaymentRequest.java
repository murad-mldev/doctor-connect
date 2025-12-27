package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import med.doctor_connect.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private UUID appointmentId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String transactionId;
    private String paymentDetails;
}
