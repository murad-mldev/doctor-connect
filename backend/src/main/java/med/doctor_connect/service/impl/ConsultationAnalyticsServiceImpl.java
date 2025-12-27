package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.model.DoctorConsultationStats;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.repository.DoctorConsultationStatsRepository;
import med.doctor_connect.repository.DoctorProfileRepository;
import med.doctor_connect.service.ConsultationAnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationAnalyticsServiceImpl implements ConsultationAnalyticsService {

    private final DoctorConsultationStatsRepository statsRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    @Override
    @Transactional
    public void updateDoctorAverageDuration(UUID doctorId, Integer consultationMinutes) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorConsultationStats stats = statsRepository.findByDoctor(doctor)
                .orElseGet(() -> DoctorConsultationStats.builder()
                        .doctor(doctor)
                        .averageConsultationMinutes(15)
                        .totalConsultations(0)
                        .totalConsultationMinutes(0L)
                        .build());

        // Update running average
        long totalMinutes = stats.getTotalConsultationMinutes() + consultationMinutes;
        int totalConsultations = stats.getTotalConsultations() + 1;
        int newAverage = (int) (totalMinutes / totalConsultations);

        stats.setTotalConsultationMinutes(totalMinutes);
        stats.setTotalConsultations(totalConsultations);
        stats.setAverageConsultationMinutes(newAverage);
        stats.setLastUpdated(new Date());

        statsRepository.save(stats);

        log.info("Updated consultation stats for doctor {}: avg={} min, total={} consultations",
                doctorId, newAverage, totalConsultations);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getEstimatedDuration(UUID doctorId) {
        return statsRepository.findByDoctorId(doctorId)
                .map(DoctorConsultationStats::getAverageConsultationMinutes)
                .orElse(15); // Default to 15 minutes
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDoctorStats(UUID doctorId) {
        DoctorConsultationStats stats = statsRepository.findByDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("No stats found for doctor"));

        Map<String, Object> response = new HashMap<>();
        response.put("doctorId", doctorId.toString());
        response.put("averageConsultationMinutes", stats.getAverageConsultationMinutes());
        response.put("totalConsultations", stats.getTotalConsultations());
        response.put("totalConsultationMinutes", stats.getTotalConsultationMinutes());
        response.put("lastUpdated", stats.getLastUpdated());

        return response;
    }
}
