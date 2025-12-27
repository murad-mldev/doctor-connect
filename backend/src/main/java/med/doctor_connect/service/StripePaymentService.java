package med.doctor_connect.service;

import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;

import java.math.BigDecimal;
import java.util.Map;

public interface StripePaymentService {

    PaymentIntent createPaymentIntent(BigDecimal amount, String currency, Map<String, Object> metadata);

    PaymentIntent createPaymentIntentWithDestination(
            BigDecimal amount,
            String currency,
            String destinationAccountId,
            BigDecimal applicationFeeAmount,
            Map<String, Object> metadata
    );

    PaymentIntent confirmPaymentIntent(String paymentIntentId);

    PaymentIntent getPaymentIntent(String paymentIntentId);

    Refund refundPayment(String paymentIntentId, BigDecimal amount);

    String getPublishableKey();
}
