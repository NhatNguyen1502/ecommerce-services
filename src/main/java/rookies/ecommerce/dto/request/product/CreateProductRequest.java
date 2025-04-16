package rookies.ecommerce.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {
  @NotNull(message = "CATEGORY_ID_REQUIRED")
  UUID categoryId;

  @NotBlank(message = "PRODUCT_NAME_REQUIRED")
  @Size(max = 200, message = "INVALID_PRODUCT_NAME")
  String name;

  String description;

  @NotNull(message = "PRODUCT_PRICE_REQUIRED")
  @Min(value = 0, message = "PRODUCT_PRICE_GREATER_THAN_ZERO")
  double price;

  @NotNull(message = "PRODUCT_QUANTITY_REQUIRED")
  @Min(value = 0, message = "PRODUCT_QUANTITY_GREATER_THAN_ZERO")
  int quantity;

  boolean isFeatured = false;
}
