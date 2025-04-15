package rookies.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rookies.ecommerce.dto.projection.product.ProductWithCategoryNameProjection;
import rookies.ecommerce.dto.request.product.CreateProductRequest;
import rookies.ecommerce.dto.request.product.UpdateProductRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.product.ProductDetailResponse;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.product.ProductServiceImpl;
import rookies.ecommerce.validation.ValidUUID;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

  ProductServiceImpl productService;

  @Operation(
      summary = "Create a new Product",
      description = "Create a new Product by providing valid product details and image.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Create Product successfully",
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
                                                              "message": "Create Product successfully"
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
                                                              "code": 1003,
                                                              "status": "fail",
                                                              "message": "Product name is required"
                                                            }
                                                            """)
                    }))
      })
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<AppApiResponse<Void>> createProduct(
      @Parameter(
              description = "Product DTO in JSON format",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
          @RequestPart
          @Valid
          CreateProductRequest product,
      @Parameter(description = "File to upload") @RequestPart MultipartFile image)
      throws IOException {

    if (image.isEmpty()) {
      throw new AppException(ErrorCode.IMAGE_REQUIRED, HttpStatus.BAD_REQUEST);
    }

    productService.createProduct(product, image);
    return ResponseEntity.status(201)
        .body(
            AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Create Product successfully")
                .build());
  }

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
  public ResponseEntity<AppApiResponse<Page<ProductWithCategoryNameProjection>>> getProducts(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    var products = productService.getActiveProducts(page, size);
    return ResponseEntity.status(200)
        .body(
            AppApiResponse.<Page<ProductWithCategoryNameProjection>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .data(products)
                .message("Get Products successfully")
                .build());
  }

  @Operation(
      summary = "Update a Product",
      description = "Update Product by providing valid product fields and optional new image.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Update Product successfully",
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
                                                       "message": "Update Product successfully"
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
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AppApiResponse<Void>> updateProduct(
      @Valid @PathVariable @ValidUUID String id,
      @RequestPart("product") @Valid UpdateProductRequest productDTO,
      @RequestPart(value = "image", required = false) MultipartFile image)
      throws IOException {

    productService.updateProduct(UUID.fromString(id), productDTO, image);
    return ResponseEntity.status(200)
        .body(
            AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Update Product successfully")
                .build());
  }

  @Operation(
      summary = "Delete a Product",
      description = "Delete a Product by id (also removes image from cloud storage)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product deleted successfully",
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
                                                      "message": "Product deleted successfully"
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
                    examples =
                        @ExampleObject(
                            value =
                                """
                                                    {
                                                      "code": 1004,
                                                      "status": "fail",
                                                      "message": "Product not found"
                                                    }
                                                    """)))
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<AppApiResponse<Void>> deleteProduct(@PathVariable String id) {
    try {
      UUID productId = UUID.fromString(id);
      productService.deleteProduct(productId);
      return ResponseEntity.ok(
          AppApiResponse.<Void>builder()
              .code(1000)
              .status(ApiStatus.SUCCESS)
              .message("Product deleted successfully")
              .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }
}
