package rookies.ecommerce.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
  @NotBlank(message = "EMAIL_REQUIRED")
  @Email(message = "EMAIL_INVALID")
  String email;

  @NotBlank(message = "PASSWORD_REQUIRED")
  String password;
}
