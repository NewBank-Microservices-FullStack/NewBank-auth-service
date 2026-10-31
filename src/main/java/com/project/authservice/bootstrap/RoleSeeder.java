package com.project.authservice.bootstrap;

import com.project.authservice.model.Enum.RoleName;
import com.project.authservice.model.Role;
import com.project.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class RoleSeeder implements DataSeeder {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void seed() {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isPresent()) {
                log.debug("Role {} already present, skipping", roleName);
                continue;
            }
            roleRepository.save(new Role(roleName));
            log.info("Seeded role {}", roleName);
        }
    }
}
