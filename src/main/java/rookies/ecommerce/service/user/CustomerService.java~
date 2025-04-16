package rookies.ecommerce.service.user;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import rookies.ecommerce.dto.request.user.CreateCustomerRequest;
import rookies.ecommerce.dto.request.user.UpdateCustomerRequest;
import rookies.ecommerce.dto.response.user.CustomerDetailResponse;
import rookies.ecommerce.dto.response.user.CustomerSumaryResponse;
import rookies.ecommerce.entity.Role;
import rookies.ecommerce.entity.user.Customer;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.CustomerRepository;
import rookies.ecommerce.repository.RoleRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService implements IUserService {

  CustomerRepository customerRepository;
  RoleRepository roleRepository;

  /**
   * Create a new customer from the given request.
   *
   * @param request the customer data.
   */
  @Override
  public void createUser(CreateCustomerRequest request) {
    Customer customer = new Customer();
    Role userRole =
        roleRepository
            .findByName("CUSTOMER")
            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, HttpStatus.NOT_FOUND));

    customer.setEmail(request.getEmail());
    customer.setPassword(request.getPassword());
    customer.setFirstName(request.getFirstName());
    customer.setLastName(request.getLastName());
    customer.setPhoneNumber(request.getPhoneNumber());
    customer.setAddress(request.getAddress());
    customer.setRole(userRole);

    customerRepository.save(customer);
  }

  /**
   * Retrieve a customer by the given ID.
   *
   * @param id the customer ID.
   * @return the customer with the given ID.
   * @throws AppException if the customer with the given ID does not exist.
   */
  @Override
  public Customer getUserById(UUID id) {
    return customerRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND));
  }

  /**
   * Update an existing customer from the given request.
   *
   * @param id the customer ID.
   * @param request the customer data.
   * @throws AppException if the customer with the given ID does not exist.
   */
  @Override
  public void updateUser(UUID id, UpdateCustomerRequest request) {
    Customer customer = getUserById(id);

    customer.setFirstName(request.getFirstName());
    customer.setLastName(request.getLastName());
    customer.setPhoneNumber(request.getPhoneNumber());
    customer.setAddress(request.getAddress());
    customer.setActive(request.isActive());

    customerRepository.save(customer);
  }

  /**
   * Delete a customer by the given ID.
   *
   * @param id the customer ID.
   * @throws AppException if the customer with the given ID does not exist.
   */
  @Override
  public void deleteUser(UUID id) {
    Customer customer = getUserById(id);
    customer.setIsDeleted(true);
    customerRepository.save(customer);
  }

  @Override
  public CustomerDetailResponse getCustomerDetails(UUID id) {
    Customer customer = getUserById(id);
    Role role = customer.getRole();
    return CustomerDetailResponse.builder()
        .id(customer.getId())
        .email(customer.getEmail())
        .phoneNumber(customer.getPhoneNumber())
        .address(customer.getAddress())
        .firstName(customer.getFirstName())
        .lastName(customer.getLastName())
        .isActive(customer.isActive())
        .roleName(role.getName())
        .createdAt(customer.getCreatedAt())
        .updatedAt(customer.getUpdatedAt())
        .build();
  }

  /**
   * Retrieves a page of active customers sorted by creation time in descending order.
   *
   * @param page the page number
   * @param size the page size
   * @return a page of active customers
   */
  @Override
  public Page<CustomerSumaryResponse> getCustomers(int page, int size) {
    return customerRepository.findAllByIsDeletedFalse(
        PageRequest.of(page, size, Sort.by("createdAt").descending()));
  }
}
