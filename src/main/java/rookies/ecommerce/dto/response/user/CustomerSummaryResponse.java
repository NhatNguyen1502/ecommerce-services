package rookies.ecommerce.dto.response.user;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSummaryResponse {
  UUID id;
  String email;
  String firstName;
  String lastName;
  String phoneNumber;
  String address;
  boolean isActive;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
