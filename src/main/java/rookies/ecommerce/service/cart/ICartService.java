package rookies.ecommerce.service.cart;

import java.util.List;
import java.util.UUID;
import rookies.ecommerce.dto.request.cart.AddToCartRequest;
import rookies.ecommerce.dto.response.cart.CartItemResponse;

public interface ICartService {
  void addToCart(AddToCartRequest request, UUID customerId);

  long getCartItemCount(UUID customerId);

  List<CartItemResponse> getCartItems(UUID customerId);

  void updateCartItemQuantity(UUID productId, UUID customerId, int quantity);
}
