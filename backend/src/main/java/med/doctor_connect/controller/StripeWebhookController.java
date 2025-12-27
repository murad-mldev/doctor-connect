package med.doctor_connect.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.config.StripeConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripeConfig stripeConfig;

    @Value("${stripe.webhook.secret:#{null}}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        // Verify webhook signature if webhook secret is configured
        if (webhookSecret != null && !webhookSecret.isEmpty()) {
            try {
                event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            } catch (SignatureVerificationException e) {
                log.error("Webhook signature verification failed: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }
        } else {
            try {
                event = Event.GSON.fromJson(payload, Event.class);
            } catch (Exception e) {
                log.error("Failed to parse webhook payload: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
            }
        }

        log.info("Received Stripe webhook event: {}", event.getType());

        // Handle different event types
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;

            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event);
                break;

            case "account.updated":
                handleAccountUpdated(event);
                break;

            case "account.external_account.created":
                handleExternalAccountCreated(event);
                break;

            case "charge.refunded":
                handleChargeRefunded(event);
                break;

            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook received");
    }

    private void handlePaymentIntentSucceeded(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        if (stripeObject instanceof PaymentIntent) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            log.info("Payment succeeded: {}", paymentIntent.getId());

            // TODO: Update appointment payment status in database
            // You can get appointmentId from paymentIntent.getMetadata().get("appointmentId")
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        if (stripeObject instanceof PaymentIntent) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            log.warn("Payment failed: {}", paymentIntent.getId());

            // TODO: Update appointment payment status to failed
            // Notify patient about payment failure
        }
    }

    private void handleAccountUpdated(Event event) {
        log.info("Stripe Connect account updated");

        // TODO: Update doctor's Stripe account status in database
        // Extract account ID from event and call stripeConnectService.updateAccountStatus()
    }

    private void handleExternalAccountCreated(Event event) {
        log.info("External account (bank account) added to Stripe Connect account");

        // This event fires when a doctor adds their bank account
        // You might want to notify the doctor or update their profile
    }

    private void handleChargeRefunded(Event event) {
        log.info("Charge refunded");

        // TODO: Handle refund logic
        // Update payment status, notify doctor and patient
    }
}
