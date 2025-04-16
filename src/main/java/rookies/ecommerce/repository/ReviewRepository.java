package rookies.ecommerce.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.dto.projection.product.review.ReviewWithUserPreviewProjection;
import rookies.ecommerce.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
  boolean existsByProductIdAndCustomerId(UUID productId, UUID customerId);

  List<ReviewWithUserPreviewProjection> findByProductId(UUID productId);
}
