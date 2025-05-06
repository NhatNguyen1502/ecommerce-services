package rookies.ecommerce.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class AddToCartRequest {
  @NotNull(message = "PRODUCT_ID_REQUIRED")
  private UUID productId;

  @NotNull(message = "QUANTITY_REQUIRED")
  @Min(value = 1, message = "QUANTITY_GREATER_THAN_ZERO")
  private int quantity;
}
