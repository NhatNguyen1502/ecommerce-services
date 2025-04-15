package rookies.ecommerce.dto.response.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSumaryResponse {
    UUID id;
    String email;
    String firstName;
    String lastName;
    String phoneNumber;
    String address;
    boolean isActive;
    String roleName;
}
