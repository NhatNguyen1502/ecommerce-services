package rookies.ecommerce.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  Category category;
  String name;
  String description;
  String imageUrl;
  double price;
  int quantity;

  @JsonProperty("isFeatured")
  boolean isFeatured;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  @Getter
  @Setter
  @Builder
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class Category {
    UUID id;
    String name;
  }
}
