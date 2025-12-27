package med.doctor_connect.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.UserDto;
import med.doctor_connect.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("GET /users - Fetching all users");
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.info("GET /users/me called");
        log.info("Session exists: {}, Session ID: {}", session != null, session != null ? session.getId() : "N/A");
        log.info("Authentication: {}", auth != null ? auth.getName() : "N/A");
        log.info("Is Authenticated: {}", auth != null && auth.isAuthenticated());
        log.info("Authorities: {}", auth != null ? auth.getAuthorities() : "N/A");

        String username = auth.getName();
        UserDto user = userService.getUserByEmailOrPhone(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        UUID userId = UUID.fromString(id);
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UserDto updateRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserDto currentUser = userService.getUserByEmailOrPhone(username);
        UUID userId = UUID.fromString(currentUser.getId());

        UserDto updatedUser = userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody UserDto updateRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(id);

        // Check if user is admin or updating their own profile
        if (!isAdminOrSelf(auth, userId)) {
            throw new AccessDeniedException("You don't have permission to update this user");
        }

        UserDto updatedUser = userService.updateUser(userId, updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        UUID userId = UUID.fromString(id);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper method to check if the current user is an admin or accessing their own resource
     */
    private boolean isAdminOrSelf(Authentication auth, UUID targetUserId) {
        // Check if user has ADMIN role
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        // Check if user is accessing their own resource
        String username = auth.getName();
        UserDto currentUser = userService.getUserByEmailOrPhone(username);
        UUID currentUserId = UUID.fromString(currentUser.getId());

        return currentUserId.equals(targetUserId);
    }
}
