package com.project.authservice.config;

import com.project.authservice.model.Enum.RoleName;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "newbank.seed")
public class DataSeedProperties {

    private boolean enabled = true;

    private DemoUser demoUser = new DemoUser();

    @Getter
    @Setter
    public static class DemoUser {
        private String username = "testuser";
        private String password = "password123";
        private String fullname = "Test User";
        private String email = "test@example.com";
        private Set<RoleName> roles = new LinkedHashSet<>(Set.of(RoleName.ROLE_USER));
    }
}
