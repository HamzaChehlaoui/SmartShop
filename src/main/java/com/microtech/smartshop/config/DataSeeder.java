package com.microtech.smartshop.config;

import com.microtech.smartshop.entity.PromoCode;
import com.microtech.smartshop.entity.User;
import com.microtech.smartshop.enums.UserRole;
import com.microtech.smartshop.repository.PromoCodeRepository;
import com.microtech.smartshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PromoCodeRepository promoCodeRepository;

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

        // Seed promo codes
        if (promoCodeRepository.count() == 0) {
            PromoCode promo1 = PromoCode.builder()
                    .code("PROMO-2025")
                    .discountPercentage(new BigDecimal("5.00"))
                    .isActive(true)
                    .validFrom(LocalDateTime.now().minusDays(30))
                    .validUntil(LocalDateTime.now().plusDays(365))
                    .maxUsages(null)
                    .build();

            PromoCode promo2 = PromoCode.builder()
                    .code("PROMO-VIP1")
                    .discountPercentage(new BigDecimal("10.00"))
                    .isActive(true)
                    .validFrom(LocalDateTime.now().minusDays(7))
                    .validUntil(LocalDateTime.now().plusDays(90))
                    .maxUsages(100)
                    .build();

            PromoCode promo3 = PromoCode.builder()
                    .code("PROMO-TEST")
                    .discountPercentage(new BigDecimal("5.00"))
                    .isActive(false)
                    .validFrom(LocalDateTime.now().minusDays(30))
                    .validUntil(LocalDateTime.now().plusDays(30))
                    .build();

            // Expired promo code for testing
            PromoCode promo4 = PromoCode.builder()
                    .code("PROMO-OLD1")
                    .discountPercentage(new BigDecimal("15.00"))
                    .isActive(true)
                    .validFrom(LocalDateTime.now().minusDays(60))
                    .validUntil(LocalDateTime.now().minusDays(1))
                    .build();

            promoCodeRepository.save(promo1);
            promoCodeRepository.save(promo2);
            promoCodeRepository.save(promo3);
            promoCodeRepository.save(promo4);

            System.out.println("Promo codes seeded: PROMO-2025, PROMO-VIP1, PROMO-TEST, PROMO-OLD1");
        }
    }
}
