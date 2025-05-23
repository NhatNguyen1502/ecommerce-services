package rookies.ecommerce.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignInResponse {
  String accessToken;
  String refreshToken;
  Object user;
}
