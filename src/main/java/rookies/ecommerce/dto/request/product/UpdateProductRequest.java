package rookies.ecommerce.dto.request.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
  @NotNull(message = "Category ID is required")
  private UUID categoryId;

  @Size(max = 200, message = "Product name must be less than 200 characters")
  String name;

  String description;

  @Min(value = 0, message = "Price must be greater than or equal to 0")
  double price;

  @Min(value = 0, message = "Quantity must be greater than or equal to 0")
  int quantity;

  boolean isFeatured = false;
}
