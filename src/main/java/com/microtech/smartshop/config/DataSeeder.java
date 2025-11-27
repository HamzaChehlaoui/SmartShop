package com.microtech.smartshop.config;

import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());

            User admin = User.builder()
                    .username("admin")
                    .password(hashedPassword)
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            System.out.println("Default Admin user created: username=admin, password=admin123");
        }
    }
}
