package com.cth.pay.api.config;

import com.cth.pay.api.entity.UserEntity;
import com.cth.pay.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                UserEntity admin = new UserEntity(
                        "admin", passwordEncoder.encode("admin123"), "admin@cth.com",
                        "System Administrator", "+1-800-555-0199", "ROLE_ADMIN"
                );
                admin.setPasswordExpiryDate(LocalDateTime.now().plusDays(90));
                userRepository.save(admin);
                System.out.println("Default Admin account created: admin / admin123");
            }
        };
    }
}
