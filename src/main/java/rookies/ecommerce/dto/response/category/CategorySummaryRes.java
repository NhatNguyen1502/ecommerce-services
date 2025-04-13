package rookies.ecommerce.dto.response.category;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategorySummaryRes {
  UUID id;
  String name;
}
