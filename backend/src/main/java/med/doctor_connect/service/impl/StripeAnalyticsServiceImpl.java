package med.doctor_connect.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.param.PaymentIntentListParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.service.StripeAnalyticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeAnalyticsServiceImpl implements StripeAnalyticsService {

    @Override
    public Map<String, Object> getPlatformEarningsSummary() {
        try {
            // Get all succeeded payment intents
            PaymentIntentListParams params = PaymentIntentListParams.builder()
                    .setLimit(100L)
                    .build();

            PaymentIntentCollection paymentIntents = PaymentIntent.list(params);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalCommission = BigDecimal.ZERO;
            BigDecimal totalDoctorPayouts = BigDecimal.ZERO;
            int totalTransactions = 0;

            for (PaymentIntent pi : paymentIntents.getData()) {
                if ("succeeded".equals(pi.getStatus())) {
                    totalTransactions++;

                    BigDecimal amount = BigDecimal.valueOf(pi.getAmount()).divide(BigDecimal.valueOf(100));
                    totalRevenue = totalRevenue.add(amount);

                    if (pi.getApplicationFeeAmount() != null) {
                        BigDecimal commission = BigDecimal.valueOf(pi.getApplicationFeeAmount()).divide(BigDecimal.valueOf(100));
                        totalCommission = totalCommission.add(commission);
                        totalDoctorPayouts = totalDoctorPayouts.add(amount.subtract(commission));
                    }
                }
            }

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalRevenue", totalRevenue);
            summary.put("totalPlatformCommission", totalCommission);
            summary.put("totalDoctorPayouts", totalDoctorPayouts);
            summary.put("totalTransactions", totalTransactions);
            summary.put("averageCommissionPerTransaction",
                    totalTransactions > 0 ? totalCommission.divide(BigDecimal.valueOf(totalTransactions), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);

            return summary;

        } catch (StripeException e) {
            log.error("Error fetching platform earnings summary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch platform earnings: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getPlatformEarningsByDateRange(Date startDate, Date endDate) {
        try {
            long startTimestamp = startDate.getTime() / 1000;
            long endTimestamp = endDate.getTime() / 1000;

            PaymentIntentListParams params = PaymentIntentListParams.builder()
                    .setCreated(
                            PaymentIntentListParams.Created.builder()
                                    .setGte(startTimestamp)
                                    .setLte(endTimestamp)
                                    .build()
                    )
                    .setLimit(100L)
                    .build();

            PaymentIntentCollection paymentIntents = PaymentIntent.list(params);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalCommission = BigDecimal.ZERO;
            int totalTransactions = 0;

            for (PaymentIntent pi : paymentIntents.getData()) {
                if ("succeeded".equals(pi.getStatus())) {
                    totalTransactions++;

                    BigDecimal amount = BigDecimal.valueOf(pi.getAmount()).divide(BigDecimal.valueOf(100));
                    totalRevenue = totalRevenue.add(amount);

                    if (pi.getApplicationFeeAmount() != null) {
                        BigDecimal commission = BigDecimal.valueOf(pi.getApplicationFeeAmount()).divide(BigDecimal.valueOf(100));
                        totalCommission = totalCommission.add(commission);
                    }
                }
            }

            Map<String, Object> earnings = new HashMap<>();
            earnings.put("startDate", startDate);
            earnings.put("endDate", endDate);
            earnings.put("totalRevenue", totalRevenue);
            earnings.put("totalPlatformCommission", totalCommission);
            earnings.put("totalTransactions", totalTransactions);

            return earnings;

        } catch (StripeException e) {
            log.error("Error fetching earnings by date range: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch earnings by date range: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getPaymentBreakdownWithCommissions(Date startDate, Date endDate) {
        try {
            long startTimestamp = startDate.getTime() / 1000;
            long endTimestamp = endDate.getTime() / 1000;

            PaymentIntentListParams params = PaymentIntentListParams.builder()
                    .setCreated(
                            PaymentIntentListParams.Created.builder()
                                    .setGte(startTimestamp)
                                    .setLte(endTimestamp)
                                    .build()
                    )
                    .setLimit(100L)
                    .build();

            PaymentIntentCollection paymentIntents = PaymentIntent.list(params);

            return paymentIntents.getData().stream()
                    .filter(pi -> "succeeded".equals(pi.getStatus()))
                    .map(pi -> {
                        Map<String, Object> breakdown = new HashMap<>();

                        BigDecimal amount = BigDecimal.valueOf(pi.getAmount()).divide(BigDecimal.valueOf(100));
                        BigDecimal commission = pi.getApplicationFeeAmount() != null
                                ? BigDecimal.valueOf(pi.getApplicationFeeAmount()).divide(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                        breakdown.put("paymentIntentId", pi.getId());
                        breakdown.put("amount", amount);
                        breakdown.put("platformCommission", commission);
                        breakdown.put("doctorPayout", amount.subtract(commission));
                        breakdown.put("currency", pi.getCurrency());
                        breakdown.put("status", pi.getStatus());
                        breakdown.put("createdAt", new Date(pi.getCreated() * 1000));
                        breakdown.put("metadata", pi.getMetadata());

                        return breakdown;
                    })
                    .collect(Collectors.toList());

        } catch (StripeException e) {
            log.error("Error fetching payment breakdown: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch payment breakdown: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getDoctorCommissionReport(String doctorProfileId) {
        try {
            PaymentIntentListParams params = PaymentIntentListParams.builder()
                    .setLimit(100L)
                    .build();

            PaymentIntentCollection paymentIntents = PaymentIntent.list(params);

            BigDecimal totalEarned = BigDecimal.ZERO;
            BigDecimal totalCommissionPaid = BigDecimal.ZERO;
            int totalAppointments = 0;

            for (PaymentIntent pi : paymentIntents.getData()) {
                if ("succeeded".equals(pi.getStatus()) &&
                    pi.getMetadata() != null &&
                    doctorProfileId.equals(pi.getMetadata().get("doctorId"))) {

                    totalAppointments++;

                    BigDecimal amount = BigDecimal.valueOf(pi.getAmount()).divide(BigDecimal.valueOf(100));
                    BigDecimal commission = pi.getApplicationFeeAmount() != null
                            ? BigDecimal.valueOf(pi.getApplicationFeeAmount()).divide(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;

                    totalEarned = totalEarned.add(amount.subtract(commission));
                    totalCommissionPaid = totalCommissionPaid.add(commission);
                }
            }

            Map<String, Object> report = new HashMap<>();
            report.put("doctorProfileId", doctorProfileId);
            report.put("totalEarned", totalEarned);
            report.put("totalCommissionPaid", totalCommissionPaid);
            report.put("totalAppointments", totalAppointments);

            return report;

        } catch (StripeException e) {
            log.error("Error fetching doctor commission report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch doctor commission report: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal getTotalPlatformCommission() {
        Map<String, Object> summary = getPlatformEarningsSummary();
        return (BigDecimal) summary.get("totalPlatformCommission");
    }

    @Override
    public BigDecimal getTotalDoctorPayouts() {
        Map<String, Object> summary = getPlatformEarningsSummary();
        return (BigDecimal) summary.get("totalDoctorPayouts");
    }
}
