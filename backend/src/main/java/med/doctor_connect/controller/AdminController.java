package med.doctor_connect.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.AdminLoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestBody AdminLoginRequest loginRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        try {
            log.info("Admin login attempt for username: {}", loginRequest.username());

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            // Ensure user is ADMIN
            boolean hasAdminRole = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!hasAdminRole) {
                log.warn("Non-admin user attempted to login via admin route: {}", loginRequest.username());
                return new ResponseEntity<>("You are not allowed to login as admin", HttpStatus.FORBIDDEN);
            }

            // Create a new SecurityContext and set the authentication
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(auth);
            SecurityContextHolder.setContext(securityContext);

            // Get or create session
            HttpSession session = request.getSession(true);
            log.info("Admin session created/retrieved - ID: {}", session.getId());
            log.info("Admin authentication successful for user: {}", auth.getName());
            log.info("Authorities: {}", auth.getAuthorities());

            // Explicitly save the SecurityContext to the session
            securityContextRepository.saveContext(securityContext, request, response);
            log.info("Admin SecurityContext saved to session");

            // Manually set the JSESSIONID cookie with proper attributes for cross-origin
            Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
            sessionCookie.setPath("/");
            sessionCookie.setMaxAge(1800); // 30 minutes
            sessionCookie.setHttpOnly(false); // Allow JS access for debugging
            sessionCookie.setSecure(false); // Use true in production with HTTPS
            response.addCookie(sessionCookie);

            log.info("Admin cookie manually set: JSESSIONID={}", session.getId());
            log.info("Admin login successful for user: {}", auth.getName());

            return ResponseEntity.ok("Admin login successful");
        } catch (Exception e) {
            log.error("Admin login failed for user: {} - Error: {}", loginRequest.username(), e.getMessage());
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutAdmin(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);

            if (session != null) {
                log.info("Logging out admin, Session ID: {}", session.getId());

                // Invalidate the session
                session.invalidate();
                log.info("Admin session invalidated");
            }

            // Clear the SecurityContext
            SecurityContextHolder.clearContext();
            log.info("Admin SecurityContext cleared");

            // Delete the JSESSIONID cookie
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);
            log.info("Admin JSESSIONID cookie deleted");

            return ResponseEntity.ok("Admin logout successful");
        } catch (Exception e) {
            log.error("Admin logout failed: {}", e.getMessage());
            return new ResponseEntity<>("Logout failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
