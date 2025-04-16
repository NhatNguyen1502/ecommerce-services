package rookies.ecommerce.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCustomerRequest {
  @NotNull(message = "EMAIL_REQUIRED")
  @Email(message = "INVALID_EMAIL")
  String email;

  @NotNull(message = "PASSWORD_REQUIRED")
  @Size(min = 8, max = 100, message = "INVALID_PASSWORD_LENGTH")
  String password;

  @NotNull(message = "FIRST_NAME_REQUIRED")
  @Size(max = 50, message = "INVALID_FIRST_NAME")
  String firstName;

  @NotNull(message = "LAST_NAME_REQUIRED")
  @Size(max = 50, message = "INVALID_LAST_NAME")
  String lastName;

  @NotNull(message = "PHONE_NUMBER_REQUIRED")
  @Pattern(
      regexp = "^(\\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])\\d{7}$",
      message = "INVALID_PHONE_NUMBER")
  String phoneNumber;

  @NotNull(message = "ADDRESS_REQUIRED")
  String address;
}
