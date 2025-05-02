package rookies.ecommerce.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import rookies.ecommerce.dto.request.user.CreateCustomerRequest;
import rookies.ecommerce.dto.request.user.UpdateCustomerRequest;
import rookies.ecommerce.dto.response.user.CustomerDetailResponse;
import rookies.ecommerce.dto.response.user.CustomerSummaryResponse;
import rookies.ecommerce.entity.Role;
import rookies.ecommerce.entity.user.Customer;
import rookies.ecommerce.entity.user.User;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.CustomerRepository;
import rookies.ecommerce.repository.RoleRepository;
import rookies.ecommerce.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService customerService;

  @Test
  void createUser_emailExistsButNotActivated_shouldThrowUnactivatedUser() {
    // Arrange
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setEmail("test@example.com");

    User existingUser = new User();
    existingUser.setActive(false);

    when(userRepository.findByEmailAndIsDeletedFalse(request.getEmail()))
        .thenReturn(Optional.of(existingUser));

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> customerService.createUser(request));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNACTIVATED_USER);
    assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(request.getEmail());
  }

  @Test
  void createUser_emailExistsAndActivated_shouldThrowEmailAlreadyExists() {
    // Arrange
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setEmail("test@example.com");

    User existingUser = new User();
    existingUser.setActive(true);

    when(userRepository.findByEmailAndIsDeletedFalse(request.getEmail()))
        .thenReturn(Optional.of(existingUser));

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> customerService.createUser(request));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(request.getEmail());
  }

  @Test
  void createUser_roleNotFound_shouldThrowRoleNotFound() {
    // Arrange
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setEmail("new@example.com");

    when(userRepository.findByEmailAndIsDeletedFalse(request.getEmail()))
        .thenReturn(Optional.empty());

    when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.empty());

    // Act & Assert
    AppException ex = assertThrows(AppException.class, () -> customerService.createUser(request));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ROLE_NOT_FOUND);
    assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalse(request.getEmail());
    verify(roleRepository, times(1)).findByName("CUSTOMER");
  }

  @Test
  void createUser_validRequest_shouldCreateCustomerSuccessfully() {
    // Arrange
    CreateCustomerRequest request = new CreateCustomerRequest();
    request.setEmail("new@example.com");
    request.setPassword("123456");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setPhoneNumber("0123456789");
    request.setAddress("123 Street");

    when(userRepository.findByEmailAndIsDeletedFalse(request.getEmail()))
        .thenReturn(Optional.empty());

    Role role = new Role();
    role.setName("CUSTOMER");

    when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(role));

    when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

    // Act
    customerService.createUser(request);

    // Assert
    verify(customerRepository, times(1))
        .save(
            argThat(
                customer ->
                    customer.getEmail().equals("new@example.com")
                        && customer.getPassword().equals("encodedPassword")
                        && customer.getFirstName().equals("John")
                        && customer.getLastName().equals("Doe")
                        && customer.getPhoneNumber().equals("0123456789")
                        && customer.getAddress().equals("123 Street")
                        && customer.getRole().equals(role)));
  }

  UUID userId = UUID.randomUUID();

  @Test
  void getUserById_validId_shouldReturnCustomer() {
    Customer customer = new Customer();
    customer.setId(userId);
    customer.setIsDeleted(false);

    when(customerRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(customer));

    Customer result = customerService.getUserById(userId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);
    verify(customerRepository, times(1)).findByIdAndIsDeletedFalse(userId);
  }

  @Test
  void getUserById_invalidId_shouldThrowUserNotFound() {
    when(customerRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.empty());

    AppException ex = assertThrows(AppException.class, () -> customerService.getUserById(userId));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void updateUser_validRequest_shouldUpdateCustomer() {
    Customer customer = new Customer();
    customer.setId(userId);

    UpdateCustomerRequest request = new UpdateCustomerRequest();
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setPhoneNumber("0123456789");
    request.setAddress("123 Street");

    when(customerRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(customer));

    customerService.updateUser(userId, request);

    assertThat(customer.getFirstName()).isEqualTo("John");
    assertThat(customer.getLastName()).isEqualTo("Doe");
    assertThat(customer.getPhoneNumber()).isEqualTo("0123456789");
    assertThat(customer.getAddress()).isEqualTo("123 Street");

    verify(customerRepository, times(1)).save(customer);
  }

  @Test
  void updateUserStatus_shouldUpdateActiveFlag() {
    Customer customer = new Customer();
    customer.setId(userId);
    customer.setActive(false);

    when(customerRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(customer));

    customerService.updateUserStatus(userId, true);

    assertThat(customer.isActive()).isTrue();
    verify(customerRepository).save(customer);
  }

  @Test
  void deleteUser_shouldSetIsDeletedToTrue() {
    Customer customer = new Customer();
    customer.setId(userId);
    customer.setIsDeleted(false);

    when(customerRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(customer));

    customerService.deleteUser(userId);

    assertThat(customer.getIsDeleted()).isTrue();
    verify(customerRepository).save(customer);
  }

  @Test
  void getCustomerDetails_shouldReturnDetailResponse() {
    Customer customer = new Customer();
    customer.setId(userId);
    customer.setEmail("test@example.com");
    customer.setPhoneNumber("0123456789");
    customer.setAddress("123 Street");
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setActive(true);
    customer.setCreatedAt(LocalDateTime.now());
    customer.setUpdatedAt(LocalDateTime.now());

    Role role = new Role();
    role.setName("CUSTOMER");
    customer.setRole(role);

    when(customerRepository.findByIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(customer));

    CustomerDetailResponse response = customerService.getCustomerDetails(userId);

    assertThat(response).isNotNull();
    assertThat(response.getEmail()).isEqualTo("test@example.com");
    assertThat(response.getFirstName()).isEqualTo("John");
    assertThat(response.getRoleName()).isEqualTo("CUSTOMER");
    verify(customerRepository).findByIdAndIsDeletedFalse(userId);
  }

  @Test
  void getCustomers_shouldReturnPagedCustomers() {
    // Arrange
    int page = 0;
    int size = 2;
    var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    // Mock data
    CustomerSummaryResponse customer1 =
        CustomerSummaryResponse.builder().firstName("John").lastName("Doe").build();
    CustomerSummaryResponse customer2 =
        CustomerSummaryResponse.builder().firstName("Jane").lastName("Smith").build();

    List<CustomerSummaryResponse> customers = List.of(customer1, customer2);
    Page<CustomerSummaryResponse> pageResult =
        new PageImpl<>(customers, pageable, customers.size());

    when(customerRepository.findAllByIsDeletedFalse(pageable)).thenReturn(pageResult);

    // Act
    Page<CustomerSummaryResponse> result = customerService.getCustomers(page, size);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
    assertThat(result.getContent().get(1).getFirstName()).isEqualTo("Jane");

    verify(customerRepository, times(1)).findAllByIsDeletedFalse(pageable);
  }

  @Test
  void getActiveUserByEmail_existentEmail_shouldReturnUser() {
    // Arrange
    String email = "existent@example.com";
    User user = new User();

    when(userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(email))
        .thenReturn(Optional.of(user));

    // Act
    User result = customerService.getActiveUserByEmail(email);

    // Assert
    assertThat(result).isNotNull().isEqualTo(user);
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalseAndIsActiveTrue(email);
  }

  @Test
  void getActiveUserByEmail_nonExistentEmail_shouldThrowException() {
    // Arrange
    String email = "nonexistent@example.com";
    when(userRepository.findByEmailAndIsDeletedFalseAndIsActiveTrue(email))
        .thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> customerService.getActiveUserByEmail(email));

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    verify(userRepository, times(1)).findByEmailAndIsDeletedFalseAndIsActiveTrue(email);
  }
}
