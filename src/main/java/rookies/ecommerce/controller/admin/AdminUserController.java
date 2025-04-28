package rookies.ecommerce.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rookies.ecommerce.dto.request.user.UpdateCustomerStatusRequest;
import rookies.ecommerce.dto.response.ApiStatus;
import rookies.ecommerce.dto.response.AppApiResponse;
import rookies.ecommerce.dto.response.user.CustomerSummaryResponse;
import rookies.ecommerce.service.user.UserService;

@RestController
@RequestMapping("/admin/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {
  UserService userService;

  @Operation(summary = "Update Customer status", description = "Update customer status by ID.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Update Customer status successfully",
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
                                                              "message": "Update Customer status successfully"
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
  @PatchMapping("/{id}/status")
  public ResponseEntity<AppApiResponse<Void>> updateCustomerStatus(
      @PathVariable String id, @RequestBody UpdateCustomerStatusRequest request) {
    userService.updateUserStatus(UUID.fromString(id), request.isActive());
    return ResponseEntity.ok(
        AppApiResponse.<Void>builder()
            .code(1000)
            .status(ApiStatus.SUCCESS)
            .message("Update Customer status successfully")
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
    userService.deleteUser(UUID.fromString(id));
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
                                                                 "content": [
                                                                   {
                                                                     "id": "c29dbaff-b341-4b9b-b4cc-1c6e1d5b4841",
                                                                     "email": "admin@gmail.com",
                                                                     "firstName": "admin",
                                                                     "lastName": "admin",
                                                                     "phoneNumber": "0396139324",
                                                                     "address": "admin",
                                                                     "roleName": "ADMIN",
                                                                     "active": true
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
                                                                 "last": true,
                                                                 "totalPages": 1,
                                                                 "totalElements": 5,
                                                                 "first": true,
                                                                 "size": 10,
                                                                 "number": 0,
                                                                 "sort": {
                                                                   "empty": false,
                                                                   "sorted": true,
                                                                   "unsorted": false
                                                                 },
                                                                 "numberOfElements": 5,
                                                                 "empty": false
                                                               }
                                                             }
                                                                        """)))
      })
  @GetMapping
  public ResponseEntity<AppApiResponse<Page<CustomerSummaryResponse>>> getAllCustomers(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    var customers = userService.getCustomers(page, size);

    return ResponseEntity.ok(
        AppApiResponse.<Page<CustomerSummaryResponse>>builder()
            .code(1000)
            .status(ApiStatus.SUCCESS)
            .message("Get Customers successfully")
            .data(customers)
            .build());
  }
}
