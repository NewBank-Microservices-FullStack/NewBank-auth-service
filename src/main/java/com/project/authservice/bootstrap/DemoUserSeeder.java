package com.project.authservice.bootstrap;

import com.project.authservice.config.DataSeedProperties;
import com.project.authservice.model.Enum.RoleName;
import com.project.authservice.model.Role;
import com.project.authservice.model.User;
import com.project.authservice.repository.RoleRepository;
import com.project.authservice.repository.UserRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class DemoUserSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSeedProperties properties;

    @Override
    @Transactional
    public void seed() {
        DataSeedProperties.DemoUser spec = properties.getDemoUser();

        if (userRepository.existsByUsername(spec.getUsername())) {
            log.info("Demo user '{}' already exists, skipping seed", spec.getUsername());
            return;
        }

        User user = new User(
                spec.getUsername(),
                passwordEncoder.encode(spec.getPassword()),
                spec.getFullname(),
                spec.getEmail());
        user.setRoles(resolveRoles(spec));

        User saved = userRepository.save(user);
        log.info("Seeded demo user '{}' (id={}, roles={})",
                saved.getUsername(), saved.getId(), spec.getRoles());
    }

    private Set<Role> resolveRoles(DataSeedProperties.DemoUser spec) {
        Set<Role> roles = new LinkedHashSet<>();
        for (RoleName roleName : spec.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalStateException(
                            "Role " + roleName + " is missing - RoleSeeder must run before DemoUserSeeder"));
            roles.add(role);
        }
        return roles;
    }
}
