package rookies.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rookies.ecommerce.dto.projection.product.IProductWithCategoryNameProjection;
import rookies.ecommerce.dto.projection.product.review.ReviewWithUserPreviewProjection;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.product.ProductDetailResponse;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.product.ProductService;
import rookies.ecommerce.service.product.review.ReviewService;
import rookies.ecommerce.service.security.JwtService;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

  ProductService productService;
  ReviewService reviewService;
  JwtService jwtService;

  @Operation(summary = "Get a Product by id", description = "Get a Product by id.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get Product successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                """
                                                            {
                                                               "code": 1000,
                                                               "status": "success",
                                                               "message": "Get Product successfully",
                                                               "data": {
                                                                 "id": "700dd3cc-b821-4812-a106-4b3b176072ef",
                                                                 "categoryId": "800ee4cc-c821-4912-a106-4b3b176072ef",
                                                                 "name": "Chocolate Cake",
                                                                 "description": "Delicious chocolate cake",
                                                                 "imageUrl": "https://cloudinary.com/image/cake.jpg",
                                                                 "price": 29.99,
                                                                 "quantity": 10,
                                                                 "isFeatured": true,
                                                                 "createdAt": "2025-04-12T05:44:38.608453",
                                                                 "updatedAt": "2025-04-12T05:44:38.608453",
                                                                 "createdBy": null,
                                                                 "updatedBy": null
                                                               }
                                                             }
                                                            """))),
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
                                                                       "code": 1005,
                                                                       "status": "fail",
                                                                       "message": "ID should be UUID"
                                                                     }
                                                                    """)
                    })),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found",
            content =
                @Content(
                    mediaType = "application/json",
                    examples = {
                      @ExampleObject(
                          value =
                              """
                                                                    {
                                                                       "code": 1004,
                                                                       "status": "fail",
                                                                       "message": "Product not found"
                                                                     }
                                                                    """)
                    }))
      })
  @GetMapping("/{id}")
  public ResponseEntity<AppApiResponse<ProductDetailResponse>> getProductById(
      @PathVariable String id) {
    try {
      UUID productId = UUID.fromString(id);

      var product = productService.getProductDetailById(productId);
      return ResponseEntity.status(200)
          .body(
              AppApiResponse.<ProductDetailResponse>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .data(product)
                  .message("Get Product successfully")
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }

  @Operation(summary = "Get Products", description = "Get paginated list of products.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get Products successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                """
                                                            {
                                                               "code": 1000,
                                                               "status": "success",
                                                               "message": "Get Products successfully",
                                                               "data": {
                                                                 "content": [
                                                                   {
                                                                     "id": "700dd3cc-b821-4812-a106-4b3b176072ef",
                                                                     "categoryId": "800ee4cc-c821-4912-a106-4b3b176072ef",
                                                                     "name": "Chocolate Cake",
                                                                     "description": "Delicious chocolate cake",
                                                                     "imageUrl": "https://cloudinary.com/image/cake.jpg",
                                                                     "price": 29.99,
                                                                     "quantity": 10,
                                                                     "isFeatured": true,
                                                                     "createdAt": "2025-04-12T05:44:38.608453",
                                                                     "updatedAt": "2025-04-12T05:44:38.608453",
                                                                     "createdBy": null,
                                                                     "updatedBy": null
                                                                   },
                                                                   {
                                                                     "id": "6e296abc-a488-4103-b76f-f62dd25fc779",
                                                                     "categoryId": "900ff4cc-d821-4912-a106-4b3b176072ef",
                                                                     "name": "Strawberry Cheesecake",
                                                                     "description": "New York style cheesecake with strawberries",
                                                                     "imageUrl": "https://cloudinary.com/image/cheesecake.jpg",
                                                                     "price": 35.99,
                                                                     "quantity": 5,
                                                                     "isFeatured": false,
                                                                     "createdAt": "2025-04-11T20:47:29.562226",
                                                                     "updatedAt": "2025-04-11T20:47:29.562226",
                                                                     "createdBy": null,
                                                                     "updatedBy": null
                                                                   }
                                                                 ],
                                                                 "pageable": {
                                                                   "pageNumber": 0,
                                                                   "pageSize": 10,
                                                                   "sort": {
                                                                     "empty": false,
                                                                     "sorted": true,
                                                                     "unsorted": false
                                                                   },
                                                                   "offset": 0,
                                                                   "paged": true,
                                                                   "unpaged": false
                                                                 },
                                                                 "totalElements": 2,
                                                                 "totalPages": 1,
                                                                 "last": true,
                                                                 "size": 10,
                                                                 "number": 0,
                                                                 "sort": {
                                                                   "empty": false,
                                                                   "sorted": true,
                                                                   "unsorted": false
                                                                 },
                                                                 "numberOfElements": 2,
                                                                 "first": true,
                                                                 "empty": false
                                                               }
                                                             }
                                                            """)))
      })
  @GetMapping
  public ResponseEntity<AppApiResponse<Page<IProductWithCategoryNameProjection>>> getProducts(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    var products = productService.getActiveProducts(page, size);
    return ResponseEntity.status(200)
        .body(
            AppApiResponse.<Page<IProductWithCategoryNameProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .data(products)
                .message("Get Products successfully")
                .build());
  }

  @Operation(
      summary = "Get featured Products",
      description = "Get paginated list of featured products.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get featured Products successfully",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                """
                                                            {
                                                               "code": 1000,
                                                               "status": "success",
                                                               "message": "Get featured Products successfully",
                                                               "data": {
                                                                 "content": [
                                                                   {
                                                                     "id": "700dd3cc-b821-4812-a106-4b3b176072ef",
                                                                     "categoryId": "800ee4cc-c821-4912-a106-4b3b176072ef",
                                                                     "name": "Chocolate Cake",
                                                                     "description": "Delicious chocolate cake",
                                                                     "imageUrl": "https://cloudinary.com/image/cake.jpg",
                                                                     "price": 29.99,
                                                                     "quantity": 10,
                                                                     "isFeatured": true,
                                                                     "createdAt": "2025-04-12T05:44:38.608453",
                                                                     "updatedAt": "2025-04-12T05:44:38.608453",
                                                                     "createdBy": null,
                                                                     "updatedBy": null
                                                                   },
                                                                   {
                                                                     "id": "6e296abc-a488-4103-b76f-f62dd25fc779",
                                                                     "categoryId": "900ff4cc-d821-4912-a106-4b3b176072ef",
                                                                     "name": "Strawberry Cheesecake",
                                                                     "description": "New York style cheesecake with strawberries",
                                                                     "imageUrl": "https://cloudinary.com/image/cheesecake.jpg",
                                                                     "price": 35.99,
                                                                     "quantity": 5,
                                                                     "isFeatured": false,
                                                                     "createdAt": "2025-04-11T20:47:29.562226",
                                                                     "updatedAt": "2025-04-11T20:47:29.562226",
                                                                     "createdBy": null,
                                                                     "updatedBy": null
                                                                   }
                                                                 ],
                                                                 "pageable": {
                                                                   "pageNumber": 0,
                                                                   "pageSize": 10,
                                                                   "sort": {
                                                                     "empty": false,
                                                                     "sorted": true,
                                                                     "unsorted": false
                                                                   },
                                                                   "offset": 0,
                                                                   "paged": true,
                                                                   "unpaged": false
                                                                 },
                                                                 "totalElements": 2,
                                                                 "totalPages": 1,
                                                                 "last": true,
                                                                 "size": 10,
                                                                 "number": 0,
                                                                 "sort": {
                                                                   "empty": false,
                                                                   "sorted": true,
                                                                   "unsorted": false
                                                                 },
                                                                 "numberOfElements": 2,
                                                                 "first": true,
                                                                 "empty": false
                                                               }
                                                             }
                                                            """)))
      })
  @GetMapping("/featured")
  public ResponseEntity<AppApiResponse<Page<IProductWithCategoryNameProjection>>>
      getFeaturedProducts(
          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    var products = productService.getFeaturedProducts(page, size);
    return ResponseEntity.status(200)
        .body(
            AppApiResponse.<Page<IProductWithCategoryNameProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .data(products)
                .message("Get featured Products successfully")
                .build());
  }

  @Operation(
      summary = "Get reviews by product",
      description = "Get all reviews for a product by providing valid product ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Get reviews for product successfully",
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
                                                                       "message": "Get reviews successfully",
                                                                       "data": [
                                                                         {
                                                                           "id": "db380520-3e31-4ba6-9128-f1f23a8fe73d",
                                                                           "content": "Good device",
                                                                           "createdAt": "2025-04-17T10:53:45.562154",
                                                                           "rating": 5,
                                                                           "customer": {
                                                                             "id": "c7320a68-72e6-4ae3-bc79-19c3330ef469",
                                                                             "fullName": "Nhật Nguyễn Lâm",
                                                                             "email": "nhat@gmail.com"
                                                                           }
                                                                         }
                                                                       ]
                                                                     }
                                                                    """)
                    }))
      })
  @GetMapping("/{productId}/reviews")
  public ResponseEntity<AppApiResponse<List<ReviewWithUserPreviewProjection>>> getReviewsByProduct(
      @PathVariable UUID productId) {

    List<ReviewWithUserPreviewProjection> reviews = reviewService.getReviewsByProduct(productId);
    return ResponseEntity.ok(
        AppApiResponse.<List<ReviewWithUserPreviewProjection>>builder()
            .code(1000)
            .status(ApiStatus.SUCCESS)
            .message("Get reviews successfully")
            .data(reviews)
            .build());
  }
}
