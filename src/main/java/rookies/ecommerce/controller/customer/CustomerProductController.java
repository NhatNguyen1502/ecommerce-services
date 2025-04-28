package rookies.ecommerce.controller.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rookies.ecommerce.dto.request.product.review.CreateReviewRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.product.review.ReviewService;
import rookies.ecommerce.service.security.JwtService;

@RestController
@RequestMapping("/customer/api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerProductController {
  JwtService jwtService;
  ReviewService reviewService;

  @Operation(
      summary = "Create Review",
      description = "Create a new Review for product by providing valid review details.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Create Review successfully",
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
                                                                                                    "message": "Create Review successfully"
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
                                                                                                     "code": 1036,
                                                                                                     "status": "fail",
                                                                                                     "message": "Rating must be between 1 and 5"
                                                                                                   }
                                                                                                  """)
                    }))
      })
  @PostMapping("/{productId}/reviews")
  public ResponseEntity<AppApiResponse<Void>> createReview(
      @Valid @RequestBody CreateReviewRequest request,
      @PathVariable String productId,
      HttpServletRequest httpServletRequest) {
    try {
      var userId = jwtService.extractUserIdFromHeader(httpServletRequest);
      reviewService.createReview(request, UUID.fromString(productId), userId);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(
              AppApiResponse.<Void>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .message("Review created successfully")
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }
}
