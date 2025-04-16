package rookies.ecommerce.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.dto.projection.product.IProductWithCategoryNameProjection;
import rookies.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  Page<IProductWithCategoryNameProjection> findAllByIsDeletedFalse(Pageable pageable);

  Optional<Product> findByIdAndIsDeletedFalse(UUID id);

  Page<IProductWithCategoryNameProjection> findAllByIsDeletedFalseAndIsFeaturedTrue(
      Pageable pageable);
}
