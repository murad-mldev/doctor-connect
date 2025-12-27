package med.doctor_connect.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.AuthRequest;
import med.doctor_connect.dto.AuthResponse;
import med.doctor_connect.dto.PatientProfileDto;
import med.doctor_connect.dto.UserDto;
import med.doctor_connect.service.ProfileService;
import med.doctor_connect.service.UserService;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final ProfileService profileService;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto dto) {
        UserDto createdUser = userService.createUser(dto);

        // Automatically create profile based on role
        if (createdUser.getRoles() != null && !createdUser.getRoles().isEmpty()) {
            UUID userId = UUID.fromString(createdUser.getId());

            boolean isPatient = createdUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("PATIENT") || role.getName().equals("ROLE_PATIENT"));
            boolean isUser = createdUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("USER") || role.getName().equals("ROLE_USER"));
            boolean isDoctor = createdUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("DOCTOR") || role.getName().equals("ROLE_DOCTOR"));

            try {
                // Create patient profile for PATIENT or USER role
                if (isPatient || isUser) {
                    PatientProfileDto patientProfile = PatientProfileDto.builder().build();
                    profileService.createPatientProfile(userId, patientProfile);
                    log.info("Patient profile created for user: {}", userId);
                }

                // Create doctor profile for DOCTOR role with license number
                if (isDoctor) {
                    log.info("Creating doctor profile for user: {} with license: {}", userId, dto.getLicenseNumber());
                    med.doctor_connect.dto.DoctorProfileDto doctorProfile = med.doctor_connect.dto.DoctorProfileDto.builder()
                        .licenseNumber(dto.getLicenseNumber())
                        .approved(false) // Requires admin verification
                        .build();
                    profileService.createDoctorProfile(userId, doctorProfile);
                    log.info("Doctor profile created - pending verification");
                }
            } catch (Exception e) {
                log.error("Failed to create profile for user {}: {}", userId, e.getMessage(), e);
                throw new RuntimeException("Failed to create profile: " + e.getMessage(), e);
            }
        }

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest loginRequest,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        try {
            log.info("Login attempt for user: {}", loginRequest.emailOrPhoneNumber());

            // Authenticate with the provided email/phone and password
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.emailOrPhoneNumber(),
                    loginRequest.password()
                )
            );

            // Create a new SecurityContext and set the authentication
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(auth);
            SecurityContextHolder.setContext(securityContext);

            // Get or create session
            HttpSession session = request.getSession(true);
            log.info("Session created/retrieved - ID: {}", session.getId());
            log.info("Authentication successful for user: {}", auth.getName());
            log.info("Authorities: {}", auth.getAuthorities());

            // Explicitly save the SecurityContext to the session
            securityContextRepository.saveContext(securityContext, request, response);
            log.info("SecurityContext saved to session");

            // Manually set the JSESSIONID cookie with proper attributes for cross-origin
            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(1800); // 30 minutes
            sessionCookie.setHttpOnly(false); // Allow JS access for debugging
            sessionCookie.setSecure(false); // Use true in production with HTTPS
            response.addCookie(sessionCookie);

            log.info("Cookie manually set: JSESSIONID={}", session.getId());

            // Ensure ADMIN cannot login here
            boolean hasAdminRole = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (hasAdminRole) {
                log.warn("Admin attempted to login via user route");
                return new ResponseEntity<>(new AuthResponse("Admin cannot login from this route", false), HttpStatus.FORBIDDEN);
            }

            // Create profile for existing users if missing (handles legacy users)
            try {
                UserDto user = userService.getUserByEmailOrPhone(loginRequest.emailOrPhoneNumber());
                UUID userId = UUID.fromString(user.getId());

                boolean isPatient = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
                boolean isUser = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
                boolean isDoctor = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

                // Create patient profile for PATIENT or USER role users
                if (isPatient || isUser) {
                    try {
                        profileService.getPatientProfileByUserId(userId);
                    } catch (RuntimeException e) {
                        // Profile doesn't exist, create it
                        log.info("Creating missing patient profile for existing user: {}", userId);
                        PatientProfileDto patientProfile = PatientProfileDto.builder().build();
                        profileService.createPatientProfile(userId, patientProfile);
                        log.info("Patient profile created for existing user: {}", userId);
                    }
                }

                // Create doctor profile for DOCTOR role users if missing
                if (isDoctor) {
                    try {
                        profileService.getDoctorProfileByUserId(userId);
                    } catch (RuntimeException e) {
                        // Profile doesn't exist, create it
                        log.info("Creating missing doctor profile for existing user: {}", userId);
                        med.doctor_connect.dto.DoctorProfileDto doctorProfile = med.doctor_connect.dto.DoctorProfileDto.builder()
                            .licenseNumber("LEGACY-LICENSE")  // Placeholder for legacy users
                            .approved(false)  // Requires admin verification
                            .build();
                        profileService.createDoctorProfile(userId, doctorProfile);
                        log.info("Doctor profile created for existing user: {}", userId);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to check/create profile on login: {}", e.getMessage());
                // Don't fail login if profile check/creation fails
            }

            log.info("Login successful for user: {}", auth.getName());
            return ResponseEntity.ok(new AuthResponse("Login successful", true));
        } catch (Exception e) {
            log.error("Login failed for user: {} - Error: {}", loginRequest.emailOrPhoneNumber(), e.getMessage());
            return new ResponseEntity<>(new AuthResponse("Invalid email/phone or password", false), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);

            if (session != null) {
                log.info("Logging out user, Session ID: {}", session.getId());

                // Invalidate the session
                session.invalidate();
                log.info("Session invalidated");
            }

            // Clear the SecurityContext
            SecurityContextHolder.clearContext();
            log.info("SecurityContext cleared");

            // Delete the JSESSIONID cookie
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);
            log.info("JSESSIONID cookie deleted");

            return ResponseEntity.ok(new AuthResponse("Logout successful", true));
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return new ResponseEntity<>(new AuthResponse("Logout failed", false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

