package rookies.ecommerce.service.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import rookies.ecommerce.entity.user.User;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

  @InjectMocks private CustomUserDetailsService customUserDetailsService;

  @Mock private UserRepository userRepository;

  private User user;
  private final String email = "test@example.com";
  private final String password = "encodedPassword";

  @BeforeEach
  void setUp() {
    // Set up a sample User entity
    user = new User();
    user.setEmail(email);
    user.setPassword(password);
    user.setActive(true);
    user.setIsDeleted(false);
  }

  @Test
  void loadUserByEmail_userFound_success() {
    // Arrange
    when(userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(email))
        .thenReturn(Optional.of(user));

    // Act
    UserDetails userDetails = customUserDetailsService.loadUserByEmail(email);

    // Assert
    assertNotNull(userDetails, "UserDetails should not be null");
    assertEquals(email, userDetails.getUsername(), "Email should match");
    assertEquals(password, userDetails.getPassword(), "Password should match");
    assertEquals(1, userDetails.getAuthorities().size(), "Should have one authority");
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalseAndIsActiveTrue(email);
  }

  @Test
  void loadUserByEmail_userNotFound_throwsAppException() {
    // Arrange
    when(userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(email))
        .thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> customUserDetailsService.loadUserByEmail(email));
    assertEquals(
        ErrorCode.USER_NOT_FOUND, exception.getErrorCode(), "Error code should be USER_NOT_FOUND");
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus(), "Status should be BAD_REQUEST");
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalseAndIsActiveTrue(email);
  }

  @Test
  void loadUserByEmail_nullEmail_throwsAppException() {
    // Arrange
    when(userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(null))
        .thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> customUserDetailsService.loadUserByEmail(null));
    assertEquals(
        ErrorCode.USER_NOT_FOUND, exception.getErrorCode(), "Error code should be USER_NOT_FOUND");
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus(), "Status should be BAD_REQUEST");
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalseAndIsActiveTrue(null);
  }

  @Test
  void loadUserByUsername_returnsNull() {
    // Act
    UserDetails userDetails = customUserDetailsService.loadUserByUsername("username");

    // Assert
    assertNull(userDetails, "loadUserByUsername should return null");
    verifyNoInteractions(userRepository);
  }
}
