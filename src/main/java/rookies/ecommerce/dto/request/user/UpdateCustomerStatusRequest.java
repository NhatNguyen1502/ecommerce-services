package rookies.ecommerce.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerStatusRequest {
  @JsonProperty("isActive")
  boolean isActive;
}
