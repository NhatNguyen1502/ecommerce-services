package rookies.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.entity.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndIsDeletedFalseAndIsActiveTrue(String email);
    Optional<User> findByIdAndIsDeletedFalseAndIsActiveTrue(UUID id);

    Optional<User> findByEmail(String email);

}
