package rookies.ecommerce.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import rookies.ecommerce.dto.request.auth.SignInRequest;
import rookies.ecommerce.dto.response.auth.SignInResponse;
import rookies.ecommerce.entity.Role;
import rookies.ecommerce.entity.user.User;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.UserRepository;
import rookies.ecommerce.service.security.JwtService;

class AuthServiceTest {

  @InjectMocks private AuthService authService;

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  private User user;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    Role role = new Role();
    role.setName("ADMIN");

    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");
    user.setPassword("hashedPassword");
    user.setRole(role);
    user.setIsDeleted(false);
    user.setActive(true);
  }

  @Test
  void authenticateUser_validCredentials_shouldReturnSignInResponse() {
    SignInRequest request = new SignInRequest();
    request.setEmail("test@example.com");
    request.setPassword("password");

    when(userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(request.getEmail()))
        .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
    when(jwtService.createToken(any(), any(), any(), eq(false))).thenReturn("access-token");
    when(jwtService.createToken(any(), any(), any(), eq(true))).thenReturn("refresh-token");

    SignInResponse response = authService.authenticateUser(request);

    assertNotNull(response);
    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", response.getRefreshToken());
    Map<String, Object> userInfo = (Map<String, Object>) response.getUser();
    assertEquals("test@example.com", userInfo.get("email"));
    assertEquals("admin", userInfo.get("role"));
  }

  @Test
  void validateUserCredentials_invalidPassword_shouldThrowAppException() {
    SignInRequest request = new SignInRequest();
    request.setEmail("test@example.com");
    request.setPassword("wrong-password");

    when(userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(request.getEmail()))
        .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              authService.validateUserCredentials(request);
            });

    assertEquals(ErrorCode.INCORRECT_PASSWORD, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void refreshToken_validToken_shouldReturnNewAccessToken() {
    String refreshToken = "valid-refresh-token";

    user.setRefreshToken(refreshToken);

    when(jwtService.extractUserIdFromToken(refreshToken)).thenReturn(user.getId());
    when(userRepository.findByIdAndIsDeletedFalseAndIsActiveTrue(user.getId()))
        .thenReturn(Optional.of(user));
    when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
    when(jwtService.createToken(user.getId(), user.getEmail(), "admin", false))
        .thenReturn("new-access-token");

    Map<String, Object> result = authService.refreshToken(refreshToken);

    assertEquals("new-access-token", result.get("accessToken"));
  }

  @Test
  void refreshToken_invalidToken_shouldThrowAppException() {
    String refreshToken = "invalid";

    lenient().when(jwtService.extractUserIdFromToken(refreshToken)).thenReturn(user.getId());
    when(userRepository.findByIdAndIsDeletedFalseAndIsActiveTrue(user.getId()))
        .thenReturn(Optional.of(user));
    user.setRefreshToken("something-else");

    when(jwtService.isRefreshToken(refreshToken)).thenReturn(false);

    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              authService.refreshToken(refreshToken);
            });

    assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
  }

  @Test
  void refreshToken_nullToken_shouldThrowAppException() {
    AppException exception =
        assertThrows(
            AppException.class,
            () -> {
              authService.refreshToken(null);
            });

    assertEquals(ErrorCode.REFRESH_TOKEN_REQUIRED, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void logoutUser_invalidToken_shouldThrowAppException() {
    String rawToken = "Bearer invalid.token.here";
    String actualToken = "invalid.token.here";

    when(jwtService.isTokenInvalid(actualToken)).thenReturn(true);

    AppException exception =
        assertThrows(AppException.class, () -> authService.logoutUser(rawToken));

    assertEquals(ErrorCode.TOKEN_ALREADY_INVALID, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

    verify(jwtService, never()).extractUserIdFromToken(any());
  }

  @Test
  void logoutUser_success() {
    String token = "Bearer valid-token";

    when(jwtService.extractUserIdFromToken("valid-token")).thenReturn(user.getId());
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(jwtService.isTokenInvalid("valid-token")).thenReturn(false);

    authService.logoutUser(token);

    verify(jwtService).invalidateToken("valid-token");
    verify(userRepository).save(user);
    assertNull(user.getRefreshToken());
  }
}
