package rookies.ecommerce.service.product.review;

import java.util.List;
import java.util.UUID;
import rookies.ecommerce.dto.projection.product.review.ReviewWithUserPreviewProjection;
import rookies.ecommerce.dto.request.product.review.CreateReviewRequest;

public interface IReviewService {
  void createReview(CreateReviewRequest request, UUID productId, UUID customerId);

  List<ReviewWithUserPreviewProjection> getReviewsByProduct(UUID productId);
}
