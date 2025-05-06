package rookies.ecommerce.service.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rookies.ecommerce.dto.request.cart.AddToCartRequest;
import rookies.ecommerce.dto.response.cart.CartItemResponse;
import rookies.ecommerce.entity.CartItem;
import rookies.ecommerce.entity.Product;
import rookies.ecommerce.entity.user.Customer;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.CartRepository;
import rookies.ecommerce.repository.OrderRepository;
import rookies.ecommerce.service.product.ProductService;
import rookies.ecommerce.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @InjectMocks private CartService cartService;

  @Mock private CartRepository cartRepository;

  @Mock private OrderRepository orderRepository;

  @Mock private UserService userService;

  @Mock private ProductService productService;

  private Customer customer;
  private Product product;
  private CartItem cartItem;
  private AddToCartRequest addToCartRequest;
  private UUID customerId;
  private UUID productId;

  @BeforeEach
  void setUp() {

    customerId = UUID.randomUUID();
    productId = UUID.randomUUID();

    // Initialize test data
    customer = new Customer();
    customer.setId(customerId);

    product = new Product();
    product.setId(productId);
    product.setPrice(50.0);
    product.setQuantity(100);

    cartItem = new CartItem();
    cartItem.setCustomer(customer);
    cartItem.setProduct(product);
    cartItem.setQuantity(2);

    addToCartRequest = new AddToCartRequest();
    addToCartRequest.setProductId(productId);
    addToCartRequest.setQuantity(3);
  }

  @Test
  void addToCart_newItem_success() {
    // Arrange
    when(userService.getUserById(customerId)).thenReturn(customer);
    when(productService.getProductById(productId)).thenReturn(product);
    when(cartRepository.findByCustomerIdAndProductId(customerId, productId))
        .thenReturn(Optional.empty());
    when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

    // Act
    assertDoesNotThrow(() -> cartService.addToCart(addToCartRequest, customerId));

    // Assert
    verify(userService, times(1)).getUserById(customerId);
    verify(productService, times(1)).getProductById(productId);
    verify(cartRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
    verify(cartRepository, times(1))
        .save(
            argThat(
                item ->
                    item.getCustomer().equals(customer)
                        && item.getProduct().equals(product)
                        && item.getQuantity() == addToCartRequest.getQuantity()));
  }

  @Test
  void addToCart_existingItem_updatesQuantity() {
    // Arrange
    when(userService.getUserById(customerId)).thenReturn(customer);
    when(productService.getProductById(productId)).thenReturn(product);
    when(cartRepository.findByCustomerIdAndProductId(customerId, productId))
        .thenReturn(Optional.of(cartItem));
    when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

    // Act
    assertDoesNotThrow(() -> cartService.addToCart(addToCartRequest, customerId));

    // Assert
    verify(cartRepository, times(1))
        .save(argThat(item -> item.getQuantity() == addToCartRequest.getQuantity()));
  }

  @Test
  void addToCart_invalidQuantity_throwsAppException() {
    // Arrange
    addToCartRequest.setQuantity(0);

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> cartService.addToCart(addToCartRequest, customerId));
    assertEquals(ErrorCode.QUANTITY_GREATER_THAN_ZERO, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(userService, never()).getUserById(any());
    verify(productService, never()).getProductById(any());
    verify(cartRepository, never()).save(any());
  }

  @Test
  void addToCart_insufficientStock_throwsAppException() {
    // Arrange
    addToCartRequest.setQuantity(101); // More than product's quantity (100)
    when(userService.getUserById(customerId)).thenReturn(customer);
    when(productService.getProductById(productId)).thenReturn(product);

    // Act & Assert
    AppException exception =
        assertThrows(AppException.class, () -> cartService.addToCart(addToCartRequest, customerId));
    assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(cartRepository, never()).save(any());
  }

  @Test
  void getCartItemCount_returnsCount() {
    // Arrange
    when(cartRepository.countByCustomerId(customerId)).thenReturn(5L);

    // Act
    long count = cartService.getCartItemCount(customerId);

    // Assert
    assertEquals(5L, count);
    verify(cartRepository, times(1)).countByCustomerId(customerId);
  }

  @Test
  void getCartItems_returnsItems() {
    // Arrange
    List<CartItemResponse> cartItems = Collections.singletonList(new CartItemResponse());
    when(cartRepository.findCartItemsByCustomerId(customerId)).thenReturn(cartItems);

    // Act
    List<CartItemResponse> result = cartService.getCartItems(customerId);

    // Assert
    assertEquals(cartItems, result);
    verify(cartRepository, times(1)).findCartItemsByCustomerId(customerId);
  }

  @Test
  void updateCartItemQuantity_validQuantity_success() {
    // Arrange
    int newQuantity = 5;
    when(cartRepository.findByCustomerIdAndProductId(customerId, productId))
        .thenReturn(Optional.of(cartItem));
    when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

    // Act
    assertDoesNotThrow(
        () -> cartService.updateCartItemQuantity(productId, customerId, newQuantity));

    // Assert
    verify(cartRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
  }

  @Test
  void updateCartItemQuantity_quantityZero_deletesItem() {
    // Arrange
    when(cartRepository.findByCustomerIdAndProductId(customerId, productId))
        .thenReturn(Optional.of(cartItem));
    doNothing().when(cartRepository).delete(cartItem);

    // Act
    assertDoesNotThrow(() -> cartService.updateCartItemQuantity(productId, customerId, 0));

    // Assert
    verify(cartRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
    verify(productService, never()).getProductById(any());
    verify(cartRepository, times(1)).delete(cartItem);
    verify(cartRepository, never()).save(any());
  }

  @Test
  void updateCartItemQuantity_negativeQuantity_throwsAppException() {
    // Arrange
    int negativeQuantity = -1;

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class,
            () -> cartService.updateCartItemQuantity(productId, customerId, negativeQuantity));
    assertEquals(ErrorCode.QUANTITY_GREATER_THAN_ZERO, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(cartRepository, never()).findByCustomerIdAndProductId(any(), any());
    verify(productService, never()).getProductById(any());
    verify(cartRepository, never()).save(any());
    verify(cartRepository, never()).delete(any());
  }

  @Test
  void updateCartItemQuantity_itemNotFound_throwsAppException() {
    // Arrange
    int newQuantity = 5;
    when(cartRepository.findByCustomerIdAndProductId(customerId, productId))
        .thenReturn(Optional.empty());

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class,
            () -> cartService.updateCartItemQuantity(productId, customerId, newQuantity));
    assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(cartRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
    verify(productService, never()).getProductById(any());
    verify(cartRepository, never()).save(any());
    verify(cartRepository, never()).delete(any());
  }

  @Test
  void updateCartItemQuantity_insufficientStock_throwsAppException() {
    // Arrange
    int newQuantity = 101; // More than product's quantity (100)
    when(cartRepository.findByCustomerIdAndProductId(customerId, productId))
        .thenReturn(Optional.of(cartItem));

    // Act & Assert
    AppException exception =
        assertThrows(
            AppException.class,
            () -> cartService.updateCartItemQuantity(productId, customerId, newQuantity));
    assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(cartRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
  }
}
