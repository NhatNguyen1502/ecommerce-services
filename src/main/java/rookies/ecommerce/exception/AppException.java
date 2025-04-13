package rookies.ecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
  private final ErrorCode errorCode;
  private final HttpStatus httpStatus;

  public AppException(ErrorCode errorCode, HttpStatus httpStatus) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }
}
