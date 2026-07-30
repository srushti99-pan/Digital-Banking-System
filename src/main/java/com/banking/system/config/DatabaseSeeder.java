package com.banking.system.config;

import com.banking.system.domain.Role;
import com.banking.system.domain.RoleType;
import com.banking.system.domain.User;
import com.banking.system.repository.RoleRepository;
import com.banking.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Ensure roles are seeded
        for (RoleType roleType : RoleType.values()) {
            if (roleRepository.findByName(roleType).isEmpty()) {
                Role role = new Role();
                role.setName(roleType);
                roleRepository.save(role);
            }
        }

        // Seed default ADMIN user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@bank.com");
            admin.setPassword(passwordEncoder.encode("admin123"));

            Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found"));
            Role customerRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                    .orElseThrow(() -> new IllegalStateException("ROLE_CUSTOMER not found"));

            HashSet<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(customerRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("--- Seeding complete: admin user created (admin / admin123) ---");
        }
    }
}
