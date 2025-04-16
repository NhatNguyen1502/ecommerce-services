package rookies.ecommerce.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.entity.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {
  boolean existsByName(String name);

  Optional<Role> findByName(String name);
}
