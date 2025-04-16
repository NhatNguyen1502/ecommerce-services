package rookies.ecommerce.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtService implements IJwtService {

  SecretKey secretKey;

  static Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

  @Value("${app.jwt.access-token-expiration-ms}")
  long accessTokenValidity;

  @Value("${app.jwt.refresh-token-expiration-ms}")
  long refreshTokenValidity;

  @Value("${app.jwt.secret}")
  String secret;

  @PostConstruct
  private void init() {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
  }

  @Override
  public String createToken(UUID userId, String email, String role, boolean isRefreshToken) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("type", isRefreshToken ? "refresh" : "access");

    if (!isRefreshToken) {
      claims.put("role", role);
      claims.put("userId", userId);
    }

    long expirationTime = isRefreshToken ? refreshTokenValidity : accessTokenValidity;

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public void invalidateToken(String token) {
    invalidatedTokens.add(token);
  }

  @Override
  public boolean validateToken(String token) {
    try {
      Claims claims = extractAllClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (AppException e) {
      throw e;
    } catch (Exception e) {
      throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public boolean isRefreshToken(String token) {
    Claims claims = extractAllClaims(token);
    return "refresh".equals(claims.get("type", String.class));
  }

  @Override
  public boolean isTokenInvalid(String token) {
    return invalidatedTokens.contains(token);
  }

  @Override
  public UUID extractUserIdFromHeader(HttpServletRequest request) {
    String token = extractTokenFromHeader(request);
    return UUID.fromString(extractAllClaims(token).get("userId", String.class));
  }

  @Override
  public String extractTokenFromHeader(HttpServletRequest request) {
    String authorizationHeader = request.getHeader("Authorization");

    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      throw new AppException(ErrorCode.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    return authorizationHeader.substring(7);
  }

  public UUID extractUserIdFromToken(String token) {
    return UUID.fromString(extractAllClaims(token).get("userId", String.class));
  }

  public String extractEmailFromToken(String token) {
    return extractAllClaims(token).getSubject();
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      throw new AppException(ErrorCode.TOKEN_ALREADY_INVALID, HttpStatus.FORBIDDEN);
    } catch (MalformedJwtException | SecurityException e) {
      throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, HttpStatus.BAD_REQUEST);
    }
  }
}
