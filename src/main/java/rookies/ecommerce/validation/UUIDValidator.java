package rookies.ecommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;

public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return false;
    }
    try {
      UUID.fromString(value);
      return true;
    } catch (IllegalArgumentException ex) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }
}
