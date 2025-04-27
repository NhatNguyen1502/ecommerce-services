package rookies.ecommerce.service.user;

import java.util.UUID;
import org.springframework.data.domain.Page;
import rookies.ecommerce.dto.request.user.CreateCustomerRequest;
import rookies.ecommerce.dto.request.user.UpdateCustomerRequest;
import rookies.ecommerce.dto.response.user.CustomerDetailResponse;
import rookies.ecommerce.dto.response.user.CustomerSummaryResponse;
import rookies.ecommerce.entity.user.User;
import rookies.ecommerce.exception.AppException;

public interface IUserService {
  /**
   * Create a new user based on the given request.
   *
   * @param request customer information
   */
  void createUser(CreateCustomerRequest request);

  /**
   * Retrieve a user by their unique identifier.
   *
   * @param id the UUID of the user to retrieve
   * @return the user with the specified UUID
   * @throws AppException if the user is not found
   */
  User getUserById(UUID id);

  /**
   * Updates an existing user with the provided details.
   *
   * @param id the unique identifier of the user to be updated
   * @param request the request containing the updated user details
   * @throws AppException if the user is not found
   */
  void updateUser(UUID id, UpdateCustomerRequest request);

  /**
   * Deletes a user by the given ID.
   *
   * @param id the UUID of the user to be deleted
   * @throws AppException if the user is not found
   */
  void deleteUser(UUID id);

  /**
   * Retrieve the customer details for a given user.
   *
   * @param id the UUID of the user to retrieve customer details for
   * @return the customer details for the user
   * @throws AppException if the user is not found
   */
  CustomerDetailResponse getCustomerDetails(UUID id);

  /**
   * Retrieves a paginated list of active customers sorted by creation time in descending order.
   *
   * @param page the page number to retrieve
   * @param size the number of customers per page
   * @return a page of customer summary responses
   */
  Page<CustomerSummaryResponse> getCustomers(int page, int size);

  /**
   * Retrieve an active user by their email address.
   *
   * @param email the email address of the user to retrieve
   * @return the user with the specified email address
   * @throws AppException if the user is not found
   */
  User getActiveUserByEmail(String email);

  /**
   * Updates the active status of an existing user.
   *
   * @param id the UUID of the user whose status is to be updated
   * @param isActive the new active status to set for the user
   * @throws AppException if the user with the given ID does not exist
   */
  void updateUserStatus(UUID id, boolean isActive);
}
