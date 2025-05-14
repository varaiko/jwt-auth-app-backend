package com.auth.config;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Order(2)
public class DefaultUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin@admin.xyz").isEmpty()) {
            User newUser = new User();
            newUser.setUsername("admin@admin.xyz");
            newUser.setPassword(passwordEncoder.encode("admin123"));
            Role userRole = roleRepository.findByName("SUPERADMIN");
            newUser.setRoles(userRole);
            userRepository.save(newUser);
        }
    }
}
