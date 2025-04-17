package rookies.ecommerce.service.security;

import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface IJwtService {
    String createToken(UUID userId, String email, String role, boolean isRefreshToken);

    void invalidateToken(String token);

    boolean validateToken(String token);

    boolean isRefreshToken(String token);

    boolean isTokenInvalid(String token);

    UUID extractUserIdFromHeader(HttpServletRequest request);

    String extractTokenFromHeader(HttpServletRequest request);
}
