package rookies.ecommerce.service.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailsService implements UserDetailsService {
  UserRepository userRepository;

  public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
    return userRepository
        .findByEmailAndIsDeletedFalseAndIsActiveTrue(email)
        .map(user -> new User(user.getEmail(), user.getPassword(), user.getAuthorities()))
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.BAD_REQUEST));
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return null;
  }
}
