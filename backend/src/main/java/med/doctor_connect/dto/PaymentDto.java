package med.doctor_connect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import med.doctor_connect.model.PaymentMethod;
import med.doctor_connect.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private String id;
    private String appointmentId;
    private String patientId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private String paymentDetails;
    private Date createdAt;
    private Date updatedAt;
}
