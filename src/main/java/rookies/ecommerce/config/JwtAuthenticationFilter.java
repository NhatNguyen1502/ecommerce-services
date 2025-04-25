package rookies.ecommerce.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.service.security.CustomUserDetailsService;
import rookies.ecommerce.service.security.JwtService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  JwtService jwtService;
  CustomUserDetailsService customUserDetailsService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath();
    return path.startsWith("/auth/")
        || path.startsWith("/swagger-ui")
        || path.startsWith("/v3/")
        || path.startsWith("/api/");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write("Authorization header is missing or invalid!");
      return;
    }

    String token = authHeader.substring(7);

    if (jwtService.isTokenInvalid(token)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write("Token has been disabled. Please log in again!");
      return;
    }

    String email;
    try {
      email = jwtService.extractEmailFromToken(token);
    } catch (AppException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
      response.getWriter().write("Token is expired!");
      return;
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = customUserDetailsService.loadUserByEmail(email);

      try {
        if (jwtService.validateToken(token)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.getWriter().write("Token validation failed!");
          return;
        }
      } catch (AppException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Token is invalid or expired!");
        return;
      }
    }

    chain.doFilter(request, response);
  }
}
