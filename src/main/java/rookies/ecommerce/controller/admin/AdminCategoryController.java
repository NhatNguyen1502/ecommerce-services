package rookies.ecommerce.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rookies.ecommerce.dto.request.category.CreateCategoryRequest;
import rookies.ecommerce.dto.request.category.UpdateCategoryRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.service.category.CategoryService;
import rookies.ecommerce.service.product.ProductService;
import rookies.ecommerce.validation.ValidUUID;

import java.util.UUID;

@RestController
@RequestMapping("/admin/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryController {
    CategoryService categoryService;
    ProductService productService;

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
            @Valid @RequestBody CreateCategoryRequest request) {

        categoryService.createCategory(request);
        return ResponseEntity.status(201)
                .body(
                        AppApiResponse.<Void>builder()
                                .code(1000)
                                .status(ApiStatus.SUCCESS)
                                .message("Create Category successfully")
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
            @Valid @PathVariable @ValidUUID String id,
            @Valid @RequestBody UpdateCategoryRequest request) {

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
