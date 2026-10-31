package com.project.NewBank.config;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.project.NewBank.model.Account;
import com.project.NewBank.model.Enum.RoleName;
import com.project.NewBank.model.Role;
import com.project.NewBank.model.User;
import com.project.NewBank.repository.AccountRepository;
import com.project.NewBank.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("local") //Only runs while developer uses
    public CommandLineRunner initializeDatabase(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder, 
            AccountRepository accountRepository) {
        
        return args -> {
            User existingUser = userRepository.findByUsername("testuser");
            
            if (existingUser != null) {
                log.info("Test user already exists — skipping seed");
                return;
            }

            try {
                // New User
                User testUser = new User();
                testUser.setUsername("testuser");
                String encodedPassword = passwordEncoder.encode("password123");
                testUser.setPassword(encodedPassword);
                testUser.setFullname("Test User");
                testUser.setEmail("test@example.com");
                
                Set<Role> roles = new HashSet<>();
                roles.add(new Role(RoleName.ROLE_USER));
                testUser.setRoles(roles);
                
                User savedUser = userRepository.save(testUser);

                // New Savings Account
                Account savingsAccount = Account.builder()
                        .accountType("SAVINGS")
                        .accountNumber("ACC170470812345612A9F3C4")
                        .user(savedUser)
                        .currency("USD")
                        .build();
                savingsAccount.setBalance(BigDecimal.valueOf(1000));
                Account savedSavings = accountRepository.save(savingsAccount);

                // New Checking Account
                Account checkingAccount = Account.builder()
                        .accountType("CHECKING")
                        .accountNumber("ACC270470812345612B8E2D5")
                        .user(savedUser)
                        .currency("USD")
                        .build();
                checkingAccount.setBalance(BigDecimal.valueOf(2500));
                Account savedChecking = accountRepository.save(checkingAccount);

                // Clean Structured Logs
                log.info("✅ Test user created successfully: username=testuser, password=password123 (ID: {})", savedUser.getId());
                log.info("✅ Savings account created: ACC170470812345612A9F3C4 | Balance: $1000 (ID: {})", savedSavings.getId());
                log.info("✅ Checking account created: ACC270470812345612B8E2D5 | Balance: $2500 (ID: {})", savedChecking.getId());

            } catch (Exception e) {
                log.error("❌ Error initializing test user and accounts: {}", e.getMessage(), e);
            }
        };
    }
}
