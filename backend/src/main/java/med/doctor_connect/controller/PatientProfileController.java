package med.doctor_connect.controller;

import lombok.RequiredArgsConstructor;
import med.doctor_connect.dto.PatientProfileDto;
import med.doctor_connect.dto.UpdateProfileRequest;
import med.doctor_connect.dto.UserDto;
import med.doctor_connect.service.ProfileService;
import med.doctor_connect.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient-profile")
@RequiredArgsConstructor
public class PatientProfileController {

    private final ProfileService profileService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<PatientProfileDto> getCurrentPatientProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        PatientProfileDto patientProfile;
        try {
            patientProfile = profileService.getPatientProfileByUserId(userId);
        } catch (RuntimeException e) {
            // Profile doesn't exist, create it automatically
            patientProfile = profileService.createPatientProfile(userId, PatientProfileDto.builder().build());
        }
        return ResponseEntity.ok(patientProfile);
    }

    @PostMapping("/me")
    public ResponseEntity<PatientProfileDto> createCurrentPatientProfile(@RequestBody PatientProfileDto profileDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        PatientProfileDto createdProfile = profileService.createPatientProfile(userId, profileDto);
        return ResponseEntity.ok(createdProfile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PatientProfileDto> getPatientProfileByUserId(@PathVariable String userId) {
        UUID id = UUID.fromString(userId);
        PatientProfileDto patientProfile = profileService.getPatientProfileByUserId(id);
        return ResponseEntity.ok(patientProfile);
    }

    @PatchMapping("/me")
    public ResponseEntity<PatientProfileDto> updateCurrentPatientProfile(@RequestBody UpdateProfileRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        PatientProfileDto updatedProfile = profileService.updatePatientProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<PatientProfileDto> updatePatientProfile(
            @PathVariable String userId,
            @RequestBody UpdateProfileRequest request) {
        UUID id = UUID.fromString(userId);
        PatientProfileDto updatedProfile = profileService.updatePatientProfile(id, request);
        return ResponseEntity.ok(updatedProfile);
    }
}
