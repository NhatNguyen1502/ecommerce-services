package rookies.ecommerce.service.cart;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService implements ICartService {
  CartRepository cartRepository;
  OrderRepository orderRepository;
  UserService userService;
  ProductService productService;

  public void addToCart(AddToCartRequest request, UUID customerId) {
    if (request.getQuantity() <= 0) {
      throw new AppException(ErrorCode.QUANTITY_GREATER_THAN_ZERO, HttpStatus.BAD_REQUEST);
    }

    Customer customer = userService.getUserById(customerId);
    Product product = productService.getProductById(request.getProductId());

    if (product.getQuantity() < request.getQuantity()) {
      throw new AppException(ErrorCode.INSUFFICIENT_STOCK, HttpStatus.BAD_REQUEST);
    }

    CartItem cartItem =
        cartRepository
            .findByCustomerIdAndProductId(customerId, request.getProductId())
            .orElse(new CartItem());

    cartItem.setCustomer(customer);
    cartItem.setProduct(product);
    cartItem.setQuantity(request.getQuantity());

    cartRepository.save(cartItem);
  }

  public long getCartItemCount(UUID customerId) {
    return cartRepository.countByCustomerId(customerId);
  }

  public List<CartItemResponse> getCartItems(UUID customerId) {
    return cartRepository.findCartItemsByCustomerId(customerId);
  }

  @Override
  public void updateCartItemQuantity(UUID productId, UUID customerId, int quantity) {
    if (quantity < 0) {
      throw new AppException(ErrorCode.QUANTITY_GREATER_THAN_ZERO, HttpStatus.BAD_REQUEST);
    }

    CartItem cartItem =
        cartRepository
            .findByCustomerIdAndProductId(customerId, productId)
            .orElseThrow(
                () -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND, HttpStatus.BAD_REQUEST));

    Product product = cartItem.getProduct();
    if (quantity > 0 && product.getQuantity() < quantity) {
      throw new AppException(ErrorCode.INSUFFICIENT_STOCK, HttpStatus.BAD_REQUEST);
    }

    if (quantity == 0) {
      cartRepository.delete(cartItem);
    } else {
      cartItem.setQuantity(quantity);
      cartRepository.save(cartItem);
    }
  }
}
