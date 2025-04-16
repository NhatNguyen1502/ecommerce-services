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
  CATEGORY_EXISTS(1006, "Category exists"),
  PRODUCT_NOT_FOUND(1007, "Product not found"),
  CATEGORY_ID_REQUIRED(1008, "Category ID is required"),
  PRODUCT_NAME_REQUIRED(1009, "Product name is required"),
  INVALID_PRODUCT_NAME(1010, "Product name is not longer than 200 characters"),
  PRODUCT_PRICE_REQUIRED(1011, "Product price is required"),
  PRODUCT_PRICE_GREATER_THAN_ZERO(1012, "Product price must be greater than zero"),
  PRODUCT_QUANTITY_REQUIRED(1013, "Product quantity is required"),
  PRODUCT_QUANTITY_GREATER_THAN_ZERO(1014, "Product quantity must be greater than zero"),
  IMAGE_REQUIRED(1015, "Image is required"),
  USER_NOT_FOUND(1016, "User not found"),
  EMAIL_REQUIRED(1017, "Email is required"),
  INVALID_EMAIL(1018, "Invalid email"),
  PASSWORD_REQUIRED(1019, "Password is required"),
  INVALID_PASSWORD_LENGTH(
      1020, "Password must be at least 8 characters and at most 100 characters"),
  FIRST_NAME_REQUIRED(1021, "First name is required"),
  INVALID_FIRST_NAME(1022, "First name is not longer than 50 characters"),
  LAST_NAME_REQUIRED(1023, "Last name is required"),
  INVALID_LAST_NAME(1024, "Last name is not longer than 50 characters"),
  PHONE_NUMBER_REQUIRED(1025, "Phone number is required"),
  ADDRESS_REQUIRED(1026, "Address is required"),
  ROLE_NOT_FOUND(1027, "Role not found");

  int code;
  String message;

  ErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
