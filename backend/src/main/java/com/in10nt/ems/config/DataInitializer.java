package com.in10nt.ems.config;

import com.in10nt.ems.model.User;
import com.in10nt.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setEmail("admin@in10nt.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            
            User ceo = new User();
            ceo.setEmail("ceo@in10nt.com");
            ceo.setPassword(passwordEncoder.encode("ceo123"));
            ceo.setFullName("CEO User");
            ceo.setRole(User.Role.CEO);
            userRepository.save(ceo);
            
            User employee = new User();
            employee.setEmail("employee@in10nt.com");
            employee.setPassword(passwordEncoder.encode("emp123"));
            employee.setFullName("Employee User");
            employee.setRole(User.Role.EMPLOYEE);
            userRepository.save(employee);
        }
    }
}
