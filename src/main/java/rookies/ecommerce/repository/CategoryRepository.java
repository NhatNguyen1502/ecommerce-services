package rookies.ecommerce.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
  Optional<Category> findByIdAndIsDeletedFalse(UUID id);

  boolean existsByNameAndIsDeletedFalse(String name);

  Page<Category> findAllByIsDeletedFalse(Pageable pageable);
}
