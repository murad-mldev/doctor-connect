package med.doctor_connect.service;

import java.util.Map;
import java.util.UUID;

public interface ConsultationAnalyticsService {

    void updateDoctorAverageDuration(UUID doctorId, Integer consultationMinutes);

    Integer getEstimatedDuration(UUID doctorId);

    Map<String, Object> getDoctorStats(UUID doctorId);
}
