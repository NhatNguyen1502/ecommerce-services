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
import rookies.ecommerce.dto.request.user.CreateCustomerRequest;
import rookies.ecommerce.dto.request.user.UpdateCustomerRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.user.CustomerDetailResponse;
import rookies.ecommerce.dto.response.user.CustomerSumaryResponse;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.service.user.CustomerService;
import rookies.ecommerce.validation.ValidUUID;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
  CustomerService customerService;

  @Operation(
      summary = "Create a new Customer",
      description = "Create a new Customer by providing valid customer details.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Create Customer successfully",
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
                      "message": "Create Customer successfully"
                    }
                """))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input provided",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                """
                    {
                                           "code": 1018,
                                           "status": "fail",
                                           "message": "Invalid email"
                                         }
                """)))
      })
  @PostMapping
  public ResponseEntity<AppApiResponse<Void>> createCustomer(
      @Valid @RequestBody CreateCustomerRequest request) {
    customerService.createUser(request);
    return ResponseEntity.status(201)
        .body(
            AppApiResponse.<Void>builder()
                .code(1000)
                .status(ApiStatus.SUCCESS)
                .message("Create Customer successfully")
                .build());
  }

  @Operation(summary = "Get a Customer by ID", description = "Retrieve a Customer using UUID")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get Customer successfully",
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
                      "message": "Get Customer successfully",
                      "data": {
                        "id": "c0a8012a-7e6a-41c1-bf80-08db3d3e5d49",
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@example.com",
                        "phoneNumber": "0123456789",
                        "address": "123 Main St"
                      }
                    }
                """))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid UUID",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                """
                    {
                      "code": 1005,
                      "status": "fail",
                      "message": "ID should be UUID"
                    }
                """)))
      })
  @GetMapping("/{id}")
  public ResponseEntity<AppApiResponse<CustomerDetailResponse>> getCustomerById(
      @PathVariable String id) {
    try {
      UUID uuid = UUID.fromString(id);
      var customer = customerService.getCustomerDetails(uuid);
      return ResponseEntity.ok(
          AppApiResponse.<CustomerDetailResponse>builder()
              .code(1000)
              .status(ApiStatus.SUCCESS)
              .message("Get Customer successfully")
              .data(customer)
              .build());
    } catch (IllegalArgumentException e) {
      throw new AppException(ErrorCode.ID_SHOULD_BE_UUID, HttpStatus.BAD_REQUEST);
    }
  }

  @Operation(summary = "Update a Customer", description = "Update an existing customer by ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Update Customer successfully",
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
                      "message": "Update Customer successfully"
                    }
                """))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid UUID",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                """
                    {
                      "code": 1005,
                      "status": "fail",
                      "message": "ID should be UUID"
                    }
                """)))
      })
  @PutMapping("/{id}")
  public ResponseEntity<AppApiResponse<Void>> updateCustomer(
      @Valid @PathVariable @ValidUUID String id,
      @Valid @RequestBody UpdateCustomerRequest request) {
    customerService.updateUser(UUID.fromString(id), request);
    return ResponseEntity.ok(
        AppApiResponse.<Void>builder()
            .code(1000)
            .status(ApiStatus.SUCCESS)
            .message("Update Customer successfully")
            .build());
  }

  @Operation(summary = "Delete a Customer", description = "Delete a customer by ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Customer deleted successfully",
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
                      "message": "Customer deleted successfully"
                    }
                """))),
        @ApiResponse(
            responseCode = "404",
            description = "Customer not found",
            content =
                @Content(
                    mediaType = "application/json",
                    examples =
                        @ExampleObject(
                            value =
                                """
                    {
                      "code": 1016,
                      "status": "fail",
                      "message": "Customer not found"
                    }
                """)))
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<AppApiResponse<Void>> deleteCustomer(@PathVariable String id) {
    customerService.deleteUser(UUID.fromString(id));
    return ResponseEntity.ok(
        AppApiResponse.<Void>builder()
            .code(1000)
            .status(ApiStatus.SUCCESS)
            .message("Customer deleted successfully")
            .build());
  }

  @Operation(summary = "Get all customers", description = "Retrieve a paginated list of customers.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get Customers successfully",
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
                                                      "message": "Get Customers successfully",
                                                      "data": {
                                                        "content": [],
                                                        "totalElements": 0,
                                                        "totalPages": 0
                                                      }
                                                    }
                                                    """)))
      })
  @GetMapping
  public ResponseEntity<AppApiResponse<Page<CustomerSumaryResponse>>> getAllCustomers(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    var customers = customerService.getCustomers(page, size);

    return ResponseEntity.ok(
        AppApiResponse.<Page<CustomerSumaryResponse>>builder()
            .code(1000)
            .status(ApiStatus.SUCCESS)
            .message("Get Customers successfully")
            .data(customers)
            .build());
  }
}
