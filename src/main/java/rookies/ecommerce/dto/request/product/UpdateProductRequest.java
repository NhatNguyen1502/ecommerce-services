package rookies.ecommerce.dto.request.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequest {
  UUID categoryId;

  @Size(max = 200, message = "INVALID_PRODUCT_NAME")
  String name;

  String description;

  @Min(value = 0, message = "PRODUCT_PRICE_GREATER_THAN_ZERO")
  double price;

  @Min(value = 0, message = "PRODUCT_QUANTITY_GREATER_THAN_ZERO")
  int quantity;

  @JsonProperty("isFeatured")
  boolean isFeatured;
}
