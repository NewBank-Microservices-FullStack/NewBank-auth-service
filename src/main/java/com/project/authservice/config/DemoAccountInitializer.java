package com.newbank.accountservice.config;

import com.newbank.accountservice.entity.Account;
import com.newbank.accountservice.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"local", "dev"})
@EnableConfigurationProperties(DemoAccountInitializer.SeedProperties.class)
public class DemoAccountInitializer {

    private final AccountRepository accountRepository;
    private final SeedProperties properties;

    @Bean
    public ApplicationRunner seedDemoAccounts() {
        return args -> {
            for (SeedProperties.AccountSpec spec : properties.getAccounts()) {
                if (accountRepository.findByAccountNumber(spec.getAccountNumber()).isPresent()) {
                    log.info("Account {} already exists, skipping", spec.getAccountNumber());
                    continue;
                }

                Account account = Account.builder()
                        .accountNumber(spec.getAccountNumber())
                        .userId(properties.getDemoUserId())
                        .accountType(spec.getAccountType())
                        .currency(spec.getCurrency())
                        .balance(spec.getBalance())
                        .status("ACTIVE")
                        .build();

                accountRepository.save(account);
                log.info("Seeded account {} (type={}, currency={}, balance={})",
                        spec.getAccountNumber(), spec.getAccountType(),
                        spec.getCurrency(), spec.getBalance());
            }
        };
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "newbank.seed")
    public static class SeedProperties {
        
        private Long demoUserId = 1L;
        private List<AccountSpec> accounts = new ArrayList<>();

        @Getter
        @Setter
        public static class AccountSpec {
            private String accountNumber;
            private String accountType;
            private String currency = "USD";
            private BigDecimal balance = BigDecimal.ZERO;
        }
    }
}
