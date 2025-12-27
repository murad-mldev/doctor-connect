package med.doctor_connect.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.config.StripeConfig;
import med.doctor_connect.service.StripePaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentServiceImpl implements StripePaymentService {

    private final StripeConfig stripeConfig;

    @Override
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, Map<String, Object> metadata) {
        try {
            // Convert amount to cents (Stripe uses smallest currency unit)
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency != null ? currency : "usd")
                    .addPaymentMethodType("card");

            if (metadata != null && !metadata.isEmpty()) {
                Map<String, String> stripeMetadata = new HashMap<>();
                metadata.forEach((key, value) -> stripeMetadata.put(key, String.valueOf(value)));
                paramsBuilder.putAllMetadata(stripeMetadata);
            }

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
            log.info("Created Stripe PaymentIntent: {}", paymentIntent.getId());

            return paymentIntent;
        } catch (StripeException e) {
            log.error("Error creating Stripe PaymentIntent: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Stripe payment intent: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentIntent createPaymentIntentWithDestination(
            BigDecimal amount,
            String currency,
            String destinationAccountId,
            BigDecimal applicationFeeAmount,
            Map<String, Object> metadata) {

        try {
            // Convert amounts to cents (Stripe uses smallest currency unit)
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
            long feeInCents = applicationFeeAmount.multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency != null ? currency : "usd")
                    .addPaymentMethodType("card")
                    .setApplicationFeeAmount(feeInCents)
                    .setTransferData(
                            PaymentIntentCreateParams.TransferData.builder()
                                    .setDestination(destinationAccountId)
                                    .build()
                    );

            if (metadata != null && !metadata.isEmpty()) {
                Map<String, String> stripeMetadata = new HashMap<>();
                metadata.forEach((key, value) -> stripeMetadata.put(key, String.valueOf(value)));
                paramsBuilder.putAllMetadata(stripeMetadata);
            }

            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
            log.info("Created Stripe PaymentIntent with destination {}: {}", destinationAccountId, paymentIntent.getId());

            return paymentIntent;
        } catch (StripeException e) {
            log.error("Error creating Stripe PaymentIntent with destination: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create Stripe payment intent with destination: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                return paymentIntent;
            }

            PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder().build();
            paymentIntent = paymentIntent.confirm(params);

            log.info("Confirmed Stripe PaymentIntent: {}", paymentIntentId);
            return paymentIntent;
        } catch (StripeException e) {
            log.error("Error confirming Stripe PaymentIntent {}: {}", paymentIntentId, e.getMessage(), e);
            throw new RuntimeException("Failed to confirm Stripe payment intent: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentIntent getPaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Error retrieving Stripe PaymentIntent {}: {}", paymentIntentId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve Stripe payment intent: " + e.getMessage(), e);
        }
    }

    @Override
    public Refund refundPayment(String paymentIntentId, BigDecimal amount) {
        try {
            RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId);

            if (amount != null) {
                long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();
                paramsBuilder.setAmount(amountInCents);
            }

            Refund refund = Refund.create(paramsBuilder.build());
            log.info("Created Stripe Refund for PaymentIntent {}: {}", paymentIntentId, refund.getId());

            return refund;
        } catch (StripeException e) {
            log.error("Error creating Stripe Refund for {}: {}", paymentIntentId, e.getMessage(), e);
            throw new RuntimeException("Failed to refund Stripe payment: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPublishableKey() {
        return stripeConfig.getPublishableKey();
    }
}
