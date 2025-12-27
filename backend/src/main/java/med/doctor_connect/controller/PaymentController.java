package med.doctor_connect.controller;

import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.CreatePaymentRequest;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.dto.PaymentDto;
import med.doctor_connect.model.PaymentStatus;
import med.doctor_connect.repository.AppointmentRepository;
import med.doctor_connect.service.PaymentService;
import med.doctor_connect.service.ProfileService;
import med.doctor_connect.service.StripePaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final StripePaymentService stripePaymentService;
    private final AppointmentRepository appointmentRepository;
    private final ProfileService profileService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PaymentDto> createPayment(@RequestBody CreatePaymentRequest request) {
        PaymentDto payment = paymentService.createPayment(request);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable String id) {
        UUID paymentId = UUID.fromString(id);
        PaymentDto payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<PaymentDto> getPaymentByAppointmentId(@PathVariable String appointmentId) {
        UUID appId = UUID.fromString(appointmentId);
        PaymentDto payment = paymentService.getPaymentByAppointmentId(appId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<PaymentDto> confirmPayment(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {

        UUID paymentId = UUID.fromString(id);
        String transactionId = request.get("transactionId");

        PaymentDto payment = paymentService.confirmPayment(paymentId, transactionId);
        return ResponseEntity.ok(payment);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PaymentDto> updatePaymentStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {

        UUID paymentId = UUID.fromString(id);
        PaymentStatus status = PaymentStatus.valueOf(request.get("status"));

        PaymentDto payment = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(payment);
    }

    // Stripe-specific endpoints

    @GetMapping("/stripe/config")
    public ResponseEntity<Map<String, String>> getStripeConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("publishableKey", stripePaymentService.getPublishableKey());
        return ResponseEntity.ok(config);
    }

    @PostMapping("/stripe/create-payment-intent")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> createStripePaymentIntent(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency = request.getOrDefault("currency", "usd").toString();

        Map<String, Object> metadata = new HashMap<>();
        if (request.containsKey("appointmentId")) {
            metadata.put("appointmentId", request.get("appointmentId").toString());
        }
        if (request.containsKey("patientId")) {
            metadata.put("patientId", request.get("patientId").toString());
        }

        PaymentIntent paymentIntent = stripePaymentService.createPaymentIntent(amount, currency, metadata);

        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        response.put("status", paymentIntent.getStatus());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stripe/confirm-payment/{paymentIntentId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> confirmStripePayment(@PathVariable String paymentIntentId) {
        PaymentIntent paymentIntent = stripePaymentService.confirmPaymentIntent(paymentIntentId);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentIntentId", paymentIntent.getId());
        response.put("status", paymentIntent.getStatus());
        response.put("amount", BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100)));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stripe/payment-intent/{paymentIntentId}")
    public ResponseEntity<Map<String, Object>> getStripePaymentIntent(@PathVariable String paymentIntentId) {
        PaymentIntent paymentIntent = stripePaymentService.getPaymentIntent(paymentIntentId);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentIntentId", paymentIntent.getId());
        response.put("status", paymentIntent.getStatus());
        response.put("amount", BigDecimal.valueOf(paymentIntent.getAmount()).divide(BigDecimal.valueOf(100)));
        response.put("currency", paymentIntent.getCurrency());
        response.put("metadata", paymentIntent.getMetadata());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stripe/refund/{paymentIntentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Map<String, Object>> refundStripePayment(
            @PathVariable String paymentIntentId,
            @RequestBody(required = false) Map<String, Object> request) {

        BigDecimal amount = null;
        if (request != null && request.containsKey("amount")) {
            amount = new BigDecimal(request.get("amount").toString());
        }

        Refund refund = stripePaymentService.refundPayment(paymentIntentId, amount);

        Map<String, Object> response = new HashMap<>();
        response.put("refundId", refund.getId());
        response.put("status", refund.getStatus());
        response.put("amount", BigDecimal.valueOf(refund.getAmount()).divide(BigDecimal.valueOf(100)));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stripe/create-appointment-payment")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> createAppointmentPaymentWithStripe(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        UUID appointmentId = UUID.fromString(request.get("appointmentId").toString());
        String currency = request.getOrDefault("currency", "usd").toString();

        // Default platform commission rate: 10%
        BigDecimal commissionRate = new BigDecimal(request.getOrDefault("commissionRate", "0.10").toString());

        // Get appointment and doctor information
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        UUID doctorUserId = appointment.getDoctor().getUser().getId();
        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(doctorUserId);

        // Check if doctor has completed Stripe onboarding
        if (!doctorProfile.isStripeOnboardingCompleted()) {
            throw new RuntimeException("Doctor has not completed Stripe onboarding");
        }

        if (doctorProfile.getStripeConnectAccountId() == null) {
            throw new RuntimeException("Doctor does not have a Stripe Connect account");
        }

        // Calculate platform commission
        BigDecimal platformFee = amount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("appointmentId", appointmentId.toString());
        metadata.put("patientId", appointment.getPatient().getId().toString());
        metadata.put("doctorId", doctorProfile.getId());
        metadata.put("commissionRate", commissionRate.toString());

        // Create payment intent with destination charge
        PaymentIntent paymentIntent = stripePaymentService.createPaymentIntentWithDestination(
                amount,
                currency,
                doctorProfile.getStripeConnectAccountId(),
                platformFee,
                metadata
        );

        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        response.put("status", paymentIntent.getStatus());
        response.put("amount", amount);
        response.put("platformFee", platformFee);
        response.put("doctorReceives", amount.subtract(platformFee));
        response.put("doctorStripeAccountId", doctorProfile.getStripeConnectAccountId());

        return ResponseEntity.ok(response);
    }
}
