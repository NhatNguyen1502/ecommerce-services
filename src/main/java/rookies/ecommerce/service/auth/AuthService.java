package rookies.ecommerce.service.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rookies.ecommerce.dto.request.auth.SignInRequest;
import rookies.ecommerce.dto.response.auth.SignInResponse;
import rookies.ecommerce.entity.user.User;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.UserRepository;
import rookies.ecommerce.service.security.JwtService;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;
  JwtService jwtService;

  static final String ACCESS_TOKEN = "access_token";
  static final String REFRESH_TOKEN = "refresh_token";

  public SignInResponse authenticateUser(SignInRequest request) {
    var authResult = validateUserCredentials(request);

    String accessToken = (String) authResult.get(ACCESS_TOKEN);
    String refreshToken = (String) authResult.get(REFRESH_TOKEN);
    Object userInfo = authResult.get("user");

    return new SignInResponse(accessToken, refreshToken, userInfo);
  }

  public Map<String, Object> validateUserCredentials(SignInRequest request) {
    User user =
        userRepository
            .findByEmailAndIsDeletedFalseAndIsActiveTrue(request.getEmail())
            .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new AppException(ErrorCode.INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
    }

    String role = Boolean.TRUE.equals(user.getRole().getName().equals("ADMIN")) ? "admin" : "user";

    String accessToken = jwtService.createToken(user.getId(), user.getEmail(), role, false);
    String refreshToken = jwtService.createToken(user.getId(), user.getEmail(), role, true);

    user.setRefreshToken(refreshToken);
    userRepository.save(user);

    Map<String, Object> response = new HashMap<>();
    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("id", user.getId());
    userInfo.put("email", user.getEmail());
    response.put("user", userInfo);
    response.put(ACCESS_TOKEN, accessToken);
    response.put(REFRESH_TOKEN, refreshToken);
    return response;
  }

  @Transactional
  public Map<String, Object> refreshToken(String refreshToken) {
    if (refreshToken == null) {
      throw new AppException(ErrorCode.REFRESH_TOKEN_REQUIRED, HttpStatus.BAD_REQUEST);
    }

    UUID userId = jwtService.extractUserIdFromToken(refreshToken);
    User user =
        userRepository
            .findByIdAndIsDeletedFalseAndIsActiveTrue(userId)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

    if (!refreshToken.equals(user.getRefreshToken()) || !jwtService.isRefreshToken(refreshToken)) {
      throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
    }

    String newAccessToken =
        jwtService.createToken(
            user.getId(),
            user.getEmail(),
            Boolean.TRUE.equals(user.getRole().getName().equals("ADMIN")) ? "admin" : "user",
            false);

    Map<String, Object> response = new HashMap<>();
    response.put(ACCESS_TOKEN, newAccessToken);
    return response;
  }

  @Transactional
  public void logoutUser(String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (jwtService.isTokenInvalid(token)) {
      throw new AppException(ErrorCode.TOKEN_ALREADY_INVALID, HttpStatus.BAD_REQUEST);
    }

    UUID userId = jwtService.extractUserIdFromToken(token);

    jwtService.invalidateToken(token);

    userRepository
        .findById(userId)
        .ifPresent(
            user -> {
              user.setRefreshToken(null);
              userRepository.save(user);
            });
  }
}
