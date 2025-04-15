package rookies.ecommerce.dto.projection.product;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IProductWithCategoryNameProjection {
  UUID getId();

  String getCategoryName();

  String getName();

  String getDescription();

  String getImageUrl();

  double getPrice();

  int getQuantity();

  boolean getIsFeatured();

  LocalDateTime getCreatedAt();

  LocalDateTime getUpdatedAt();

  interface Category {
    String getName();
  }
}
