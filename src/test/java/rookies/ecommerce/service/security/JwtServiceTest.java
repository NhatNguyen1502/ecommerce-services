package rookies.ecommerce.service.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @InjectMocks private JwtService jwtService;

  @Mock private HttpServletRequest request;

  private SecretKey secretKey;
  private final String secret = "mySecretKeyForTestingPurposes1234567890";
  private final UUID userId = UUID.randomUUID();
  private final String email = "test@example.com";
  private final String role = "USER";

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    // Set up secret key
    secretKey = Keys.hmacShaKeyFor(secret.getBytes());

    // Use reflection to set private fields
    setField(jwtService, "secretKey", secretKey);
    setField(jwtService, "secret", secret);
    // 1 hour
    long accessTokenValidity = 1000 * 60 * 60;
    setField(jwtService, "accessTokenValidity", accessTokenValidity);
    // 1 day
    long refreshTokenValidity = 1000 * 60 * 60 * 24;
    setField(jwtService, "refreshTokenValidity", refreshTokenValidity);

    // Clear invalidated tokens
    setField(jwtService, "invalidatedTokens", ConcurrentHashMap.newKeySet());
  }

  private void invokeInitMethod() {
    try {
      java.lang.reflect.Method initMethod = JwtService.class.getDeclaredMethod("init");
      initMethod.setAccessible(true);
      initMethod.invoke(jwtService);
    } catch (Exception e) {
      throw new RuntimeException("Failed to invoke init method", e);
    }
  }

  private void setField(Object target, String fieldName, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  @Test
  void init_setsSecretKey_success() throws NoSuchFieldException, IllegalAccessException {
    // Invoke the init method via reflection or rely on @InjectMocks to trigger @PostConstruct
    // Since @InjectMocks triggers @PostConstruct, we reset secretKey in setUp and call init
    // explicitly
    setField(jwtService, "secretKey", null); // Ensure secretKey is null before init
    invokeInitMethod();

    // Verify secretKey is set
    Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
    secretKeyField.setAccessible(true);
    SecretKey actualSecretKey = (SecretKey) secretKeyField.get(jwtService);

    assertNotNull(actualSecretKey, "SecretKey should be initialized");
    // Verify the key is equivalent to one generated from the secret
    SecretKey expectedSecretKey = Keys.hmacShaKeyFor(secret.getBytes());
    assertEquals(
        expectedSecretKey.getAlgorithm(),
        actualSecretKey.getAlgorithm(),
        "SecretKey algorithm should match");
  }

  @Test
  void createToken_accessToken_success() {
    String token = jwtService.createToken(userId, email, role, false);

    Claims claims =
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

    assertEquals(email, claims.getSubject());
    assertEquals(userId.toString(), claims.get("userId"));
    assertEquals(role, claims.get("role"));
    assertEquals("access", claims.get("type"));
    assertTrue(claims.getExpiration().after(new Date()));
  }

  @Test
  void createToken_refreshToken_success() {
    String token = jwtService.createToken(userId, email, role, true);

    Claims claims =
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

    assertEquals(email, claims.getSubject());
    assertEquals(userId.toString(), claims.get("userId"));
    assertEquals("refresh", claims.get("type"));
    assertNull(claims.get("role"));
    assertTrue(claims.getExpiration().after(new Date()));
  }

  @Test
  void validateToken_validToken_success() {
    String token = jwtService.createToken(userId, email, role, false);
    assertTrue(jwtService.validateToken(token));
  }

  @Test
  void validateToken_expiredToken_throwsException() {
    // Create a token with a short expiration time
    setFieldSafe(jwtService, "accessTokenValidity", 1L);
    String token = jwtService.createToken(userId, email, role, false);

    // Wait for token to expire
    sleep(10);

    AppException exception =
        assertThrows(AppException.class, () -> jwtService.validateToken(token));
    assertEquals(ErrorCode.TOKEN_ALREADY_INVALID, exception.getErrorCode());
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
  }

  @Test
  void validateToken_invalidToken_throwsException() {
    String invalidToken = "invalid.token.here";
    AppException exception =
        assertThrows(AppException.class, () -> jwtService.validateToken(invalidToken));
    assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void validateToken_nullToken_throwsAppException() {
    AppException exception = assertThrows(AppException.class, () -> jwtService.validateToken(null));
    assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void isRefreshToken_refreshToken_returnsTrue() {
    String token = jwtService.createToken(userId, email, role, true);
    assertTrue(jwtService.isRefreshToken(token));
  }

  @Test
  void isRefreshToken_accessToken_returnsFalse() {
    String token = jwtService.createToken(userId, email, role, false);
    assertFalse(jwtService.isRefreshToken(token));
  }

  @Test
  void invalidateToken_tokenAddedToInvalidatedList() {
    String token = jwtService.createToken(userId, email, role, false);
    jwtService.invalidateToken(token);
    assertTrue(jwtService.isTokenInvalid(token));
  }

  @Test
  void isTokenInvalid_nonInvalidatedToken_returnsFalse() {
    String token = jwtService.createToken(userId, email, role, false);
    assertFalse(jwtService.isTokenInvalid(token));
  }

  @Test
  void extractUserIdFromHeader_validHeader_success() {
    String token = jwtService.createToken(userId, email, role, false);
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

    UUID extractedUserId = jwtService.extractUserIdFromHeader(request);
    assertEquals(userId, extractedUserId);
  }

  @Test
  void extractUserIdFromHeader_missingHeader_throwsException() {
    when(request.getHeader("Authorization")).thenReturn(null);

    AppException exception =
        assertThrows(AppException.class, () -> jwtService.extractUserIdFromHeader(request));
    assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
  }

  @Test
  void extractUserIdFromHeader_invalidHeader_throwsException() {
    when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

    AppException exception =
        assertThrows(AppException.class, () -> jwtService.extractUserIdFromHeader(request));
    assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
  }

  @Test
  void extractTokenFromHeader_validHeader_success() {
    String token = jwtService.createToken(userId, email, role, false);
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

    String extractedToken = jwtService.extractTokenFromHeader(request);
    assertEquals(token, extractedToken);
  }

  @Test
  void extractUserIdFromToken_validToken_success() {
    String token = jwtService.createToken(userId, email, role, false);
    UUID extractedUserId = jwtService.extractUserIdFromToken(token);
    assertEquals(userId, extractedUserId);
  }

  @Test
  void extractEmailFromToken_validToken_success() {
    String token = jwtService.createToken(userId, email, role, false);
    String extractedEmail = jwtService.extractEmailFromToken(token);
    assertEquals(email, extractedEmail);
  }

  private void setFieldSafe(Object target, String fieldName, Object value) {
    try {
      setField(target, fieldName, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to set field: " + fieldName, e);
    }
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
