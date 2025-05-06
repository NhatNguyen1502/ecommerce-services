package rookies.ecommerce.controller.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rookies.ecommerce.dto.request.cart.AddToCartRequest;
import rookies.ecommerce.dto.request.cart.UpdateCartItemQuantityRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.cart.CartItemResponse;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.cart.CartService;
import rookies.ecommerce.service.security.JwtService;

@RestController
@RequestMapping("/customer/api/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerCartController {
  JwtService jwtService;
  CartService cartService;

  @Operation(
      summary = "Add Product to Cart",
      description = "Add a product to the customer's shopping cart with specified quantity.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Product added to cart successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1000,
                                                                      "status": "success",
                                                                      "message": "Product added to cart successfully"
                                                                    }
                                                                    """)
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input provided",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1037,
                                                                      "status": "fail",
                                                                      "message": "Quantity must be greater than 0"
                                                                    }
                                                                    """)
                    }))
      })
  @PostMapping("/add")
  public ResponseEntity<AppApiResponse<Void>> addToCart(
      @Valid @RequestBody AddToCartRequest request, HttpServletRequest httpServletRequest) {
    try {
      var userId = jwtService.extractUserIdFromHeader(httpServletRequest);
      cartService.addToCart(request, userId);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              AppApiResponse.<Void>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .message("Product added to cart successfully")
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }

  @Operation(
      summary = "Get Cart Item Count",
      description = "Retrieve the total number of items in the customer's cart.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cart item count retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1000,
                                                                      "status": "success",
                                                                      "message": "Cart item count retrieved successfully",
                                                                      "data": 3
                                                                    }
                                                                    """)
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid customer ID",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1001,
                                                                      "status": "fail",
                                                                      "message": "Invalid UUID format"
                                                                    }
                                                                    """)
                    }))
      })
  @GetMapping("/count")
  public ResponseEntity<AppApiResponse<Long>> getCartItemCount(
      HttpServletRequest httpServletRequest) {
    try {
      var userId = jwtService.extractUserIdFromHeader(httpServletRequest);
      long count = cartService.getCartItemCount(userId);
      return ResponseEntity.ok()
          .body(
              AppApiResponse.<Long>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .message("Cart item count retrieved successfully")
                  .data(count)
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }

  @Operation(
      summary = "Get Cart Items",
      description = "Retrieve the list of cart items for the authenticated customer.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cart items retrieved successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1000,
                                                                      "status": "success",
                                                                      "message": "Cart items retrieved successfully",
                                                                      "data": [
                                                                        {
                                                                          "productId": "123e4567-e89b-12d3-a456-426614174000",
                                                                          "productName": "Product 1",
                                                                          "quantity": 2,
                                                                          "price": 29.99,
                                                                          "imageUrl": "http://example.com/product1.jpg"
                                                                        },
                                                                        {
                                                                          "productId": "987fcdeb-12ab-34cd-5678-426614174000",
                                                                          "productName": "Product 2",
                                                                          "quantity": 1,
                                                                          "price": 49.99,
                                                                          "imageUrl": "http://example.com/product2.jpg"
                                                                        }
                                                                      ]
                                                                    }
                                                                    """)
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid customer ID",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1001,
                                                                      "status": "fail",
                                                                      "message": "Invalid UUID format"
                                                                    }
                                                                    """)
                    }))
      })
  @GetMapping
  public ResponseEntity<AppApiResponse<List<CartItemResponse>>> getCartItems(
      HttpServletRequest httpServletRequest) {
    try {
      var userId = jwtService.extractUserIdFromHeader(httpServletRequest);
      List<CartItemResponse> cartItems = cartService.getCartItems(userId);
      return ResponseEntity.ok()
          .body(
              AppApiResponse.<List<CartItemResponse>>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .message("Cart items retrieved successfully")
                  .data(cartItems)
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }

  @Operation(
      summary = "Update Cart Item Quantity",
      description =
          "Update the quantity of a specific product in the customer's cart. If quantity is 0, the item is removed.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cart item quantity updated successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1000,
                                                                      "status": "success",
                                                                      "message": "Cart item quantity updated successfully"
                                                                    }
                                                                    """)
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input, cart item not found, or insufficient stock",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1039,
                                                                      "status": "fail",
                                                                      "message": "Cart item not found"
                                                                    }
                                                                    """),
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1040,
                                                                      "status": "fail",
                                                                      "message": "Insufficient stock"
                                                                    }
                                                                    """),
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                      "code": 1037,
                                                                      "status": "fail",
                                                                      "message": "Quantity must be greater than 0"
                                                                    }
                                                                    """)
                    }))
      })
  @PatchMapping("/update-quantity")
  public ResponseEntity<AppApiResponse<Void>> updateCartItemQuantity(
      @Valid @RequestBody UpdateCartItemQuantityRequest request,
      HttpServletRequest httpServletRequest) {
    try {
      var userId = jwtService.extractUserIdFromHeader(httpServletRequest);
      cartService.updateCartItemQuantity(request.getProductId(), userId, request.getQuantity());
      return ResponseEntity.ok()
          .body(
              AppApiResponse.<Void>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .message("Cart item quantity updated successfully")
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }
}
