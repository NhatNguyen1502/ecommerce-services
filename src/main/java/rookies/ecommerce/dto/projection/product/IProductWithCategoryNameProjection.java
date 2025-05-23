package rookies.ecommerce.dto.projection.product;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IProductWithCategoryNameProjection {
  UUID getId();

  Category getCategory();

  String getName();

  String getDescription();

  String getImageUrl();

  double getPrice();

  int getQuantity();

  boolean getIsFeatured();

  LocalDateTime getCreatedAt();

  LocalDateTime getUpdatedAt();

  Double getAverageRating();

  Long getRatingCount();

  interface Category {
    UUID getId();

    String getName();
  }
}
