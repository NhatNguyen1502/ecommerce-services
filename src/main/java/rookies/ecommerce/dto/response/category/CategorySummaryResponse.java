package rookies.ecommerce.dto.response.category;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategorySummaryResponse {
  UUID id;
  String name;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
