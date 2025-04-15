package rookies.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.entity.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    boolean existsByName(String name);
    Optional<Role> findByName(String name);
}
