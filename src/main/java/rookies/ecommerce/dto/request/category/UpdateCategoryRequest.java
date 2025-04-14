package rookies.ecommerce.dto.request.category;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCategoryRequest {
  @Size(max = 25, message = "INVALID_CATEGORY_NAME")
  String name;
}
