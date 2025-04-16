package rookies.ecommerce.dto.response.product.review;

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
public class ReviewResponse {
  UUID id;
  String content;
  Integer rating;
  LocalDateTime createdAt;
  UUID customerId;
  String customerName;
  UUID productId;
}
