package med.doctor_connect.config;

import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.dto.PatientProfileDto;
import med.doctor_connect.model.Role;
import med.doctor_connect.model.User;
import med.doctor_connect.repository.RoleRepository;
import med.doctor_connect.repository.UserRepository;
import med.doctor_connect.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileService profileService;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        Role patientRole = roleRepository.findByName("ROLE_PATIENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PATIENT")));
        Role doctorRole = roleRepository.findByName("ROLE_DOCTOR")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_DOCTOR")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // Create User
        if (userRepository.findByUsername("murad_user@gmail.com").isEmpty()) {
            User user = new User();
            user.setUsername("murad_user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setFullName("Regular User");
            user.setPhone("+1234567890");
            user.setActive(true);
            user.setVerified(true);
            user.setRoles(Set.of(userRole));
            User savedUser = userRepository.save(user);

            // Create patient profile for USER role
            try {
                PatientProfileDto patientProfile = PatientProfileDto.builder().build();
                profileService.createPatientProfile(savedUser.getId(), patientProfile);
                log.info("Patient profile created for USER role test user");
            } catch (Exception e) {
                log.error("Failed to create profile for test user: {}", e.getMessage());
            }
        }

        // Create patient user
        if (userRepository.findByUsername("patient@gmail.com").isEmpty()) {
            User patient = new User();
            patient.setUsername("patient@gmail.com");
            patient.setPassword(passwordEncoder.encode("patient"));
            patient.setFullName("John Patient");
            patient.setPhone("+1987654321");
            patient.setActive(true);
            patient.setVerified(true);
            patient.setRoles(Set.of(patientRole));
            User savedPatient = userRepository.save(patient);

            // Create patient profile
            try {
                PatientProfileDto patientProfile = PatientProfileDto.builder().build();
                profileService.createPatientProfile(savedPatient.getId(), patientProfile);
                log.info("Patient profile created for PATIENT role test user");
            } catch (Exception e) {
                log.error("Failed to create profile for test patient: {}", e.getMessage());
            }
        }

        // Create doctor user
        if (userRepository.findByUsername("doctor@gmail.com").isEmpty()) {
            User doctor = new User();
            doctor.setUsername("doctor@gmail.com");
            doctor.setPassword(passwordEncoder.encode("doctor"));
            doctor.setFullName("Dr. Smith");
            doctor.setPhone("+1122334455");
            doctor.setActive(true);
            doctor.setVerified(true);
            doctor.setRoles(Set.of(doctorRole));
            userRepository.save(doctor);
        }

        // Create admin user
        if (userRepository.findByUsername("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setFullName("Admin User");
            admin.setPhone("+1555666777");
            admin.setActive(true);
            admin.setVerified(true);
            admin.setRoles(Set.of(adminRole, userRole)); // Admin also has user role
            userRepository.save(admin);
        }
    }
}
