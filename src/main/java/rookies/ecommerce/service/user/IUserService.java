package rookies.ecommerce.service.user;

import org.springframework.data.domain.Page;
import rookies.ecommerce.dto.request.user.CreateCustomerRequest;
import rookies.ecommerce.dto.request.user.UpdateCustomerRequest;
import rookies.ecommerce.dto.response.user.CustomerDetailResponse;
import rookies.ecommerce.dto.response.user.CustomerSumaryResponse;
import rookies.ecommerce.entity.user.User;
import rookies.ecommerce.exception.AppException;

import java.util.List;
import java.util.UUID;

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

    Page<CustomerSumaryResponse> getCustomers(int page, int size);
}