package med.doctor_connect.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StripeAnalyticsService {

    Map<String, Object> getPlatformEarningsSummary();

    Map<String, Object> getPlatformEarningsByDateRange(Date startDate, Date endDate);

    List<Map<String, Object>> getPaymentBreakdownWithCommissions(Date startDate, Date endDate);

    Map<String, Object> getDoctorCommissionReport(String doctorProfileId);

    BigDecimal getTotalPlatformCommission();

    BigDecimal getTotalDoctorPayouts();
}
