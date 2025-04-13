package rookies.ecommerce.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
  UNCATEGORIZED_EXCEPTION(1001, "An unexpected error has occurred"),
  INVALID_CATEGORY_NAME(1002, "Category name is not longer than 25 characters"),
  CATEGORY_NAME_REQUIRED(1003, "Category name is required"),
  CATEGORY_NOT_FOUND(1004, "Category not found"),
  ID_SHOULD_BE_UUID(1005, "ID should be UUID"),
  CATEGORY_EXISTS(1006, "Category exists");

  int code;
  String message;

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
