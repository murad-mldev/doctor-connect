package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.service.StripeAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform-earnings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PlatformEarningsController {

    private final StripeAnalyticsService stripeAnalyticsService;

    // Stripe Earnings Endpoints

    @GetMapping("/stripe/summary")
    public ResponseEntity<Map<String, Object>> getStripePlatformSummary() {
        Map<String, Object> summary = stripeAnalyticsService.getPlatformEarningsSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/stripe/by-date-range")
    public ResponseEntity<Map<String, Object>> getStripeEarningsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        Map<String, Object> earnings = stripeAnalyticsService.getPlatformEarningsByDateRange(startDate, endDate);
        return ResponseEntity.ok(earnings);
    }

    @GetMapping("/stripe/payment-breakdown")
    public ResponseEntity<List<Map<String, Object>>> getStripePaymentBreakdown(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        List<Map<String, Object>> breakdown = stripeAnalyticsService.getPaymentBreakdownWithCommissions(startDate, endDate);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/stripe/doctor/{doctorProfileId}")
    public ResponseEntity<Map<String, Object>> getDoctorStripeCommissionReport(@PathVariable String doctorProfileId) {
        Map<String, Object> report = stripeAnalyticsService.getDoctorCommissionReport(doctorProfileId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/stripe/total-commission")
    public ResponseEntity<Map<String, Object>> getTotalStripeCommission() {
        BigDecimal totalCommission = stripeAnalyticsService.getTotalPlatformCommission();

        Map<String, Object> response = new HashMap<>();
        response.put("totalPlatformCommission", totalCommission);
        response.put("paymentMethod", "STRIPE");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stripe/total-doctor-payouts")
    public ResponseEntity<Map<String, Object>> getTotalDoctorPayouts() {
        BigDecimal totalPayouts = stripeAnalyticsService.getTotalDoctorPayouts();

        Map<String, Object> response = new HashMap<>();
        response.put("totalDoctorPayouts", totalPayouts);
        response.put("paymentMethod", "STRIPE");

        return ResponseEntity.ok(response);
    }

    // TODO: Add endpoints for other payment methods (CASH, BKASH, CARD)
    // These would query the Payment entity in your database

    @GetMapping("/all-methods/summary")
    public ResponseEntity<Map<String, Object>> getAllPaymentMethodsSummary() {
        // TODO: Implement combined summary from all payment methods
        // This would include Stripe + CASH + BKASH + CARD from Payment table

        Map<String, Object> combinedSummary = new HashMap<>();

        // Stripe earnings
        Map<String, Object> stripeData = stripeAnalyticsService.getPlatformEarningsSummary();

        // TODO: Add database queries for other payment methods
        // Example:
        // - Query Payment table where method = CASH
        // - Query Payment table where method = BKASH
        // - Query Payment table where method = CARD

        combinedSummary.put("stripe", stripeData);
        combinedSummary.put("message", "Other payment methods analytics coming soon");

        return ResponseEntity.ok(combinedSummary);
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Stripe stats
        Map<String, Object> stripeSummary = stripeAnalyticsService.getPlatformEarningsSummary();

        stats.put("totalStripeRevenue", stripeSummary.get("totalRevenue"));
        stats.put("totalStripePlatformCommission", stripeSummary.get("totalPlatformCommission"));
        stats.put("totalStripeDoctorPayouts", stripeSummary.get("totalDoctorPayouts"));
        stats.put("totalStripeTransactions", stripeSummary.get("totalTransactions"));

        // TODO: Add other payment method stats from database
        stats.put("message", "Add CASH/BKASH/CARD stats from Payment table");

        return ResponseEntity.ok(stats);
    }
}
