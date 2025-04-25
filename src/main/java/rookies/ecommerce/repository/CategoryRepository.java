package rookies.ecommerce.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.dto.response.category.CategorySummaryResponse;
import rookies.ecommerce.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
  Optional<Category> findByIdAndIsDeletedFalse(UUID id);

  boolean existsByNameAndIsDeletedFalse(String name);

  List<CategorySummaryResponse> findAllByIsDeletedFalseOrderByCreatedAtDesc();
}
