package rookies.ecommerce.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateProductRequest {
  @NotNull(message = "CATEGORY_ID_REQUIRED")
  private UUID categoryId;

  @NotBlank(message = "PRODUCT_NAME_REQUIRED")
  @Size(max = 200, message = "INVALID_PRODUCT_NAME")
  private String name;

  private String description;

  @NotNull(message = "PRODUCT_PRICE_REQUIRED")
  @Min(value = 0, message = "PRODUCT_PRICE_GREATER_THAN_ZERO")
  private double price;

  @NotNull(message = "PRODUCT_QUANTITY_REQUIRED")
  @Min(value = 0, message = "PRODUCT_QUANTITY_GREATER_THAN_ZERO")
  private int quantity;

  private boolean isFeatured = false;
}
