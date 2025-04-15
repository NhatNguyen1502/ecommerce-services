package rookies.ecommerce.dto.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCustomerRequest {
    @Size(max = 50, message = "INVALID_FIRST_NAME")
    private String firstName;

    @Size(max = 50, message = "INVALID_LAST_NAME")
    private String lastName;

    @Pattern(
            regexp = "^(\\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])\\d{7}$",
            message = "INVALID_PHONE_NUMBER"
    )
    String phoneNumber;

    String address;

    boolean isActive;
}
