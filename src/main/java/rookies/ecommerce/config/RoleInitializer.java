package rookies.ecommerce.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rookies.ecommerce.entity.Role;
import rookies.ecommerce.repository.RoleRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleInitializer implements CommandLineRunner {

    RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (!roleRepository.existsByName("ADMIN")) {
            Role adminRole = new Role();
            adminRole.setId(1);
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
        }
        if (!roleRepository.existsByName("CUSTOMER")) {
            Role userRole = new Role();
            userRole.setId(2);
            userRole.setName("CUSTOMER");
            roleRepository.save(userRole);
        }
    }
}
