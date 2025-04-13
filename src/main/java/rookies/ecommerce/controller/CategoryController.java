package rookies.ecommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rookies.ecommerce.dto.request.category.CreateCategoryReq;
import rookies.ecommerce.dto.request.category.UpdateCategoryReq;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.category.CategorySummaryRes;
import rookies.ecommerce.entity.Category;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.impl.CategoryServiceImpl;
import rookies.ecommerce.validation.ValidUUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
  CategoryServiceImpl categoryService;

  @Operation(
      summary = "Create a new Category",
      description = "Create a new Category by providing valid category details.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Create Category successfully",
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
                                                                      "message": "Create Category successfully"
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
                                                                      "message": "Category name is required"
                                                                    }
                                                                    """)
                    }))
      })
  @PostMapping
  public ResponseEntity<AppApiResponse<Void>> createCategory(
      @Valid @RequestBody CreateCategoryReq request) {

    categoryService.createCategory(request);
    return ResponseEntity.status(201)
        .body(
            AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Create Category successfully")
                .build());
  }

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
  public ResponseEntity<AppApiResponse<CategorySummaryRes>> getCategoryById(
      @PathVariable String id) {
    try {
      UUID categoryId = UUID.fromString(id);

      var category = categoryService.getCategoryById(categoryId);
      return ResponseEntity.status(200)
          .body(
              AppApiResponse.<CategorySummaryRes>builder()
                  .code(1000)
                  .status(ApiStatus.SUCCESS)
                  .data(category)
                  .message("Get Category successfully")
                  .build());
    } catch (Exception e) {
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
  public ResponseEntity<AppApiResponse<Page<Category>>> getCategories(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    var categories = categoryService.getCategories(page, size);
    return ResponseEntity.status(200)
        .body(
            AppApiResponse.<Page<Category>>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .data(categories)
                .message("Get Categories successfully")
                .build());
  }

  @Operation(
      summary = "Update a Category",
      description = "Update Category by providing valid category fields need to update.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Create Category successfully",
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
                                                               "message": "Update Category successfully"
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
  @PutMapping("/{id}")
  public ResponseEntity<AppApiResponse<Void>> updateCategory(
      @Valid @PathVariable @ValidUUID String id, @Valid @RequestBody UpdateCategoryReq request) {

    categoryService.updateCategory(UUID.fromString(id), request);
    return ResponseEntity.status(200)
        .body(
            AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Update Category successfully")
                .build());
  }

  @Operation(summary = "Delete a Category", description = "Delete a Category by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Category deleted successfully",
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
                                                              "message": "Create Category successfully"
                                                            }
                                                            """))),
        @ApiResponse(
            responseCode = "404",
            description = "Category not found",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                "{\"code\": 1016, \"status\": \"fail\", \"message\": \"Badge not found\"}")))
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<AppApiResponse<Void>> deleteCategory(@PathVariable String id) {
    categoryService.deleteCategory(UUID.fromString(id));
    return ResponseEntity.ok(
        AppApiResponse.<Void>builder()
            .code(1000)
            .status(ApiStatus.SUCCESS)
            .message("Category deleted successfully")
            .build());
  }
}
