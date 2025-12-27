package med.doctor_connect.controller;

import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.DoctorProfileDto;
import med.doctor_connect.dto.UpdateProfileRequest;
import med.doctor_connect.dto.UserDto;
import med.doctor_connect.service.ProfileService;
import med.doctor_connect.service.StripeConnectService;
import med.doctor_connect.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/doctor-profile")
@RequiredArgsConstructor
public class DoctorProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final StripeConnectService stripeConnectService;

    @GetMapping("/me")
    public ResponseEntity<DoctorProfileDto> getCurrentDoctorProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            log.info("GET /doctor-profile/me - User: {}", username);
            log.info("Authorities: {}", auth.getAuthorities());

            UserDto user = userService.getUserByEmailOrPhone(username);
            UUID userId = UUID.fromString(user.getId());

            log.info("Found user with ID: {}", userId);

            DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);

            log.info("Successfully retrieved doctor profile for user: {}", userId);
            return ResponseEntity.ok(doctorProfile);
        } catch (Exception e) {
            log.error("Failed to get current doctor profile", e);
            throw e;
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DoctorProfileDto> getDoctorProfileByUserId(@PathVariable String userId) {
        UUID id = UUID.fromString(userId);
        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(id);
        return ResponseEntity.ok(doctorProfile);
    }

    @PatchMapping("/me")
    public ResponseEntity<DoctorProfileDto> updateCurrentDoctorProfile(@RequestBody UpdateProfileRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        DoctorProfileDto updatedProfile = profileService.updateDoctorProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<DoctorProfileDto> updateDoctorProfile(
            @PathVariable String userId,
            @RequestBody UpdateProfileRequest request) {
        UUID id = UUID.fromString(userId);
        DoctorProfileDto updatedProfile = profileService.updateDoctorProfile(id, request);
        return ResponseEntity.ok(updatedProfile);
    }

    // Stripe Connect endpoints

    @PostMapping("/stripe/create-account")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> createStripeConnectAccount(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        String email = request.getOrDefault("email", user.getEmail());
        String country = request.getOrDefault("country", "US");

        Account account = stripeConnectService.createConnectAccount(doctorProfileId, email, country);

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", account.getId());
        response.put("status", "created");
        response.put("message", "Stripe Connect account created. Complete onboarding to start receiving payments.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stripe/onboarding-link")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> getStripeOnboardingLink(@RequestBody Map<String, String> request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        String refreshUrl = request.get("refreshUrl");
        String returnUrl = request.get("returnUrl");

        AccountLink accountLink = stripeConnectService.createAccountOnboardingLink(doctorProfileId, refreshUrl, returnUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("url", accountLink.getUrl());
        response.put("expiresAt", accountLink.getExpiresAt());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stripe/dashboard-link")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> getStripeDashboardLink() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        LoginLink loginLink = stripeConnectService.createDashboardLink(doctorProfileId);

        Map<String, Object> response = new HashMap<>();
        response.put("url", loginLink.getUrl());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stripe/account-status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> getStripeAccountStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        Account account = stripeConnectService.getAccountStatus(doctorProfileId);

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", account.getId());
        response.put("chargesEnabled", account.getChargesEnabled());
        response.put("payoutsEnabled", account.getPayoutsEnabled());
        response.put("detailsSubmitted", account.getDetailsSubmitted());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stripe/refresh-status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> refreshStripeAccountStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto user = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(user.getId());

        DoctorProfileDto doctorProfile = profileService.getDoctorProfileByUserId(userId);
        UUID doctorProfileId = UUID.fromString(doctorProfile.getId());

        stripeConnectService.updateAccountStatus(doctorProfileId);

        DoctorProfileDto updatedProfile = profileService.getDoctorProfileByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("stripeAccountStatus", updatedProfile.getStripeAccountStatus());
        response.put("stripeOnboardingCompleted", updatedProfile.isStripeOnboardingCompleted());

        return ResponseEntity.ok(response);
    }
}
