package med.doctor_connect.service.impl;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.dto.PatientProfileDto;
import med.doctor_connect.dto.UpdateProfileRequest;
import med.doctor_connect.mapper.DoctorProfileMapper;
import med.doctor_connect.mapper.PatientProfileMapper;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.model.PatientProfile;
import med.doctor_connect.model.User;
import med.doctor_connect.repository.DoctorProfileRepository;
import med.doctor_connect.repository.PatientProfileRepository;
import med.doctor_connect.repository.UserRepository;
import med.doctor_connect.service.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;
    private final DoctorProfileMapper doctorMapper = DoctorProfileMapper.INSTANCE;
    private final PatientProfileMapper patientMapper = PatientProfileMapper.INSTANCE;

    @Override
    public DoctorProfileDto getDoctorProfileByUserId(UUID userId) {
        DoctorProfile profile = doctorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return doctorMapper.toDto(profile);
    }

    @Override
    public PatientProfileDto getPatientProfileByUserId(UUID userId) {
        PatientProfile profile = patientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return patientMapper.toDto(profile);
    }

    @Override
    public DoctorProfileDto updateDoctorProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoctorProfile profile = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        // Update user fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        // Update doctor profile fields
        if (request.getDescription() != null) {
            profile.setDescription(request.getDescription());
        }
        if (request.getSpecialization() != null) {
            profile.setSpecialization(request.getSpecialization());
        }
        if (request.getDesignation() != null) {
            profile.setDesignation(request.getDesignation());
        }
        if (request.getQualifications() != null) {
            profile.setQualifications(request.getQualifications());
        }

        userRepository.save(user);
        DoctorProfile updated = doctorProfileRepository.save(profile);
        return doctorMapper.toDto(updated);
    }

    @Override
    public PatientProfileDto updatePatientProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile profile = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        // Update user fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        // Update patient profile fields
        if (request.getDateOfBirth() != null) {
            profile.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getBloodGroup() != null) {
            profile.setBloodGroup(request.getBloodGroup());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (request.getEmergencyContact() != null) {
            profile.setEmergencyContact(request.getEmergencyContact());
        }

        userRepository.save(user);
        PatientProfile updated = patientProfileRepository.save(profile);
        return patientMapper.toDto(updated);
    }

    @Override
    public DoctorProfileDto createDoctorProfile(UUID userId, DoctorProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (doctorProfileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Doctor profile already exists");
        }

        DoctorProfile profile = DoctorProfile.builder()
                .user(user)
                .description(profileDto.getDescription())
                .specialization(profileDto.getSpecialization())
                .designation(profileDto.getDesignation())
                .qualifications(profileDto.getQualifications())
                .licenseNumber(profileDto.getLicenseNumber())
                .approved(false)
                .build();

        DoctorProfile saved = doctorProfileRepository.save(profile);
        return doctorMapper.toDto(saved);
    }

    @Override
    public PatientProfileDto createPatientProfile(UUID userId, PatientProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (patientProfileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Patient profile already exists");
        }

        PatientProfile profile = PatientProfile.builder()
                .user(user)
                .dateOfBirth(profileDto.getDateOfBirth())
                .gender(profileDto.getGender())
                .bloodGroup(profileDto.getBloodGroup())
                .address(profileDto.getAddress())
                .emergencyContact(profileDto.getEmergencyContact())
                .build();

        PatientProfile saved = patientProfileRepository.save(profile);
        return patientMapper.toDto(saved);
    }
}
