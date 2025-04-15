package rookies.ecommerce.dto.response.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDetailResponse {
    UUID id;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
    String address;
    boolean isActive;
    String roleName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
