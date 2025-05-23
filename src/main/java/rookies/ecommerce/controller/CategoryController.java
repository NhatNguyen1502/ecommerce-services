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
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.category.CategorySummaryResponse;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.category.CategoryService;
import rookies.ecommerce.service.product.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
  CategoryService categoryService;
  ProductService productService;

  @Operation(summary = "Get a Category by id", description = "Get a Category by id.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get Category successfully",
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
                                                               "message": "Get Category successfully",
                                                               "data": {
                                                                 "id": "700dd3cc-b821-4812-a106-4b3b176072ef",
                                                                 "name": "Desserts"
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
                    }))
      })
  @GetMapping("/{id}")
  public ResponseEntity<AppApiResponse<CategorySummaryResponse>> getCategoryById(
      @PathVariable String id) {
    try {
      UUID categoryId = UUID.fromString(id);

      var category = categoryService.getActiveCategoryById(categoryId);
      return ResponseEntity.status(200)
          .body(
              AppApiResponse.<CategorySummaryResponse>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .data(category)
                  .message("Get Category successfully")
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }

  @Operation(summary = "Get Categories", description = "Get Categories.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get Categories successfully",
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
                                                               "message": "Get Categories successfully",
                                                               "data": {
                                                                 "content": [
                                                                   {
                                                                     "id": "700dd3cc-b821-4812-a106-4b3b176072ef",
                                                                     "createdAt": "2025-04-12T05:44:38.608453",
                                                                     "updatedAt": "2025-04-12T05:44:38.608453",
                                                                     "createdBy": null,
                                                                     "updatedBy": null,
                                                                     "isDeleted": false,
                                                                     "name": "Desserts"
                                                                   },
                                                                   {
                                                                     "id": "6e296abc-a488-4103-b76f-f62dd25fc779",
                                                                     "createdAt": "2025-04-11T20:47:29.562226",
                                                                     "updatedAt": "2025-04-11T20:47:29.562226",
                                                                     "createdBy": null,
                                                                     "updatedBy": null,
                                                                     "isDeleted": false,
                                                                     "name": "Appertize"
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
  public ResponseEntity<AppApiResponse<List<CategorySummaryResponse>>> getCategories() {

    var categories = categoryService.getActiveCategories();
    return ResponseEntity.status(200)
        .body(
            AppApiResponse.<List<CategorySummaryResponse>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .data(categories)
                .message("Get Categories successfully")
                .build());
  }

  @Operation(
      summary = "Get Products by Category",
      description = "Get paginated list of products by category.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get Products by category successfully",
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
                                                                "message": "Get Products by category successfully",
                                                                "data": {
                                                                  "content": [
                                                                    {
                                                                      "name": "Xiaomi Mi12s Pro",
                                                                      "id": "ce67783c-df17-4ee0-a67f-f039d79fc9fe",
                                                                      "description": "Flaship 2021",
                                                                      "categoryName": "Xiaomi",
                                                                      "isFeatured": true,
                                                                      "price": 22000000,
                                                                      "quantity": 20,
                                                                      "createdAt": "2025-04-16T11:36:38.664976",
                                                                      "updatedAt": "2025-04-16T11:36:38.664976",
                                                                      "imageUrl": "http://res.cloudinary.com/dqgsplqrj/image/upload/v1744778198/ad5hhkbsnlwar3u8zqwl.png"
                                                                    }
                                                                  ],
                                                                  "pageable": {
                                                                    "pageNumber": 0,
                                                                    "pageSize": 10,
                                                                    "sort": {
                                                                      "empty": true,
                                                                      "sorted": false,
                                                                      "unsorted": true
                                                                    },
                                                                    "offset": 0,
                                                                    "paged": true,
                                                                    "unpaged": false
                                                                  },
                                                                  "last": true,
                                                                  "totalPages": 1,
                                                                  "totalElements": 1,
                                                                  "first": true,
                                                                  "size": 10,
                                                                  "number": 0,
                                                                  "sort": {
                                                                    "empty": true,
                                                                    "sorted": false,
                                                                    "unsorted": true
                                                                  },
                                                                  "numberOfElements": 1,
                                                                  "empty": false
                                                                }
                                                              }
                                                            """)))
      })
  @GetMapping("/{categoryId}/products")
  public ResponseEntity<AppApiResponse<Page<IProductWithCategoryNameProjection>>>
      getProductsByCategory(
          @PathVariable() String categoryId,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
    try {
      var id = UUID.fromString(categoryId);
      var products = productService.getProductsByCategory(page, size, id);
      return ResponseEntity.status(200)
          .body(
              AppApiResponse.<Page<IProductWithCategoryNameProjection>>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .data(products)
                  .message("Get Products by category successfully")
                  .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }
}
