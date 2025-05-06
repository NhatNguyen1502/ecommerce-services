package rookies.ecommerce.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemQuantityRequest {
  @NotNull(message = "PRODUCT_ID_REQUIRED")
  private UUID productId;

  @Min(value = 0, message = "QUANTITY_GREATER_THAN_OR_EQUAL_ZERO")
  private int quantity;
}
