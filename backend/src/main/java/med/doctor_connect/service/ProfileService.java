package med.doctor_connect.service;

import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.dto.PatientProfileDto;
import med.doctor_connect.dto.UpdateProfileRequest;

import java.util.UUID;

public interface ProfileService {

    DoctorProfileDto getDoctorProfileByUserId(UUID userId);

    PatientProfileDto getPatientProfileByUserId(UUID userId);

    DoctorProfileDto updateDoctorProfile(UUID userId, UpdateProfileRequest request);

    PatientProfileDto updatePatientProfile(UUID userId, UpdateProfileRequest request);

    DoctorProfileDto createDoctorProfile(UUID userId, DoctorProfileDto profileDto);

    PatientProfileDto createPatientProfile(UUID userId, PatientProfileDto profileDto);
}
