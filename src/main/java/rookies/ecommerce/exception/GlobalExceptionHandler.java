package rookies.ecommerce.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  /**
   * Handle {@link RuntimeException}.
   *
   * @param exception a runtime exception
   * @return a HTTP response with status code 500 and a JSON body with error code 500 and status
   *     {@link ApiStatus#FAIL}
   */
  @ExceptionHandler(value = RuntimeException.class)
  public ResponseEntity<AppApiResponse<Void>> handleRuntimeException(RuntimeException exception) {
    log.error(exception.getMessage(), exception);

    AppApiResponse<Void> appApiResponse =
        AppApiResponse.<Void>builder()
            .code(500)
            .message("Internal server error 1")
            .status(ApiStatus.FAIL)
            .build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(appApiResponse);
  }

  /**
   * Handle {@link AppException}.
   *
   * @param exception an AppException
   * @return a HTTP response with status code of the exception and a JSON body with error code of
   *     the exception and status {@link ApiStatus#FAIL}
   */
  @ExceptionHandler(value = AppException.class)
  public ResponseEntity<AppApiResponse<Void>> handleAppException(AppException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    AppApiResponse<Void> appApiResponse =
        AppApiResponse.<Void>builder()
            .code(errorCode.getCode())
            .status(ApiStatus.FAIL)
            .message(errorCode.getMessage())
            .build();
    return ResponseEntity.status(exception.getHttpStatus()).body(appApiResponse);
  }

  /**
   * Handle {@link MethodArgumentNotValidException} which is thrown by Spring when a field of an
   * object passed to a controller method is not valid.
   *
   * <p>The method will return a HTTP response with status code 400 and a JSON body with error code
   * of the corresponding enum value in {@link ErrorCode} and status {@link ApiStatus#FAIL}.
   *
   * <p>If the error is not valid enum value in {@link ErrorCode}, the method will return a HTTP
   * response with status code 500 and a JSON body with error code 500 and status {@link
   * ApiStatus#FAIL}.
   *
   * @param exception a MethodArgumentNotValidException
   * @return a HTTP response with status code and a JSON body
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<AppApiResponse<Void>> handleValidationException(
      MethodArgumentNotValidException exception) {
    String enumService =
        exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : "";
    ErrorCode errorCode;
    try {
      errorCode = ErrorCode.valueOf(enumService);
      log.info(enumService);
    } catch (IllegalArgumentException e) {
      log.error(exception.getMessage(), exception);

      AppApiResponse<Void> appApiResponse =
          AppApiResponse.<Void>builder()
              .code(500)
              .message("Internal server error 2")
              .status(ApiStatus.FAIL)
              .build();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(appApiResponse);
    }

    AppApiResponse<Void> appApiResponse =
        AppApiResponse.<Void>builder()
            .code(errorCode.getCode())
            .status(ApiStatus.FAIL)
            .message(errorCode.getMessage())
            .build();
    return ResponseEntity.badRequest().body(appApiResponse);
  }
}
