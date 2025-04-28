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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rookies.ecommerce.dto.request.product.CreateProductRequest;
import rookies.ecommerce.dto.request.product.UpdateProductRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.product.ProductService;
import rookies.ecommerce.validation.ValidUUID;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminProductController {
    ProductService productService;

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
