package com.project.authservice.config;

import com.project.authservice.bootstrap.DataSeeder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StopWatch;


@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"local", "dev"})
@EnableConfigurationProperties(DataSeedProperties.class)
public class DataInitializer {

    private final List<DataSeeder> seeders; 

    @Bean
    @ConditionalOnProperty(prefix = "newbank.seed", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public ApplicationRunner seedDataRunner() {
        return args -> {
            if (seeders.isEmpty()) {
                log.debug("No DataSeeder beans registered, nothing to seed");
                return;
            }

            StopWatch stopWatch = new StopWatch("data-seed");
            stopWatch.start();

            for (DataSeeder seeder : seeders) {
                String seederName = seeder.getClass().getSimpleName();
                log.info("Running seeder {}", seederName);
                try {
                    seeder.seed();
                } catch (RuntimeException ex) {
                    // Fail fast: a half-seeded database is worse than a failed boot.
                    throw new IllegalStateException("Data seeding failed in " + seederName, ex);
                }
            }

            stopWatch.stop();
            log.info("Data seeding finished: {} seeder(s) in {} ms",
                    seeders.size(), stopWatch.getTotalTimeMillis());
        };
    }
}
