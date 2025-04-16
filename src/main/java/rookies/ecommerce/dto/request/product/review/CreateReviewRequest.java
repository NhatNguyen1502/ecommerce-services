package rookies.ecommerce.dto.request.product.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateReviewRequest {
  String content;

  @Min(value = 1, message = "INVALID_RATING")
  @Max(value = 5, message = "INVALID_RATING")
  Integer rating;
}
