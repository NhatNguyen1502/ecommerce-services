package rookies.ecommerce.dto.response.product;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
  UUID id;
  String categoryName;
  UUID categoryId;
  String name;
  String description;
  String imageUrl;
  double price;
  int quantity;
  boolean isFeatured;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
