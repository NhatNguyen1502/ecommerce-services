package rookies.ecommerce.service.product.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import rookies.ecommerce.dto.projection.product.review.ReviewWithUserPreviewProjection;
import rookies.ecommerce.dto.request.product.review.CreateReviewRequest;
import rookies.ecommerce.entity.Product;
import rookies.ecommerce.entity.Review;
import rookies.ecommerce.entity.user.Customer;
import rookies.ecommerce.exception.AppException;
import rookies.ecommerce.exception.ErrorCode;
import rookies.ecommerce.repository.ReviewRepository;
import rookies.ecommerce.service.product.ProductService;
import rookies.ecommerce.service.user.UserService;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock private ReviewRepository reviewRepository;
  @Mock private UserService userService;
  @Mock private ProductService productService;

  @InjectMocks private ReviewService reviewService;

  @Test
  void createReview_validRequest_shouldSaveReview() {
    UUID productId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    CreateReviewRequest request = new CreateReviewRequest();
    request.setContent("Great product");
    request.setRating(5);

    Customer customer = new Customer();
    Product product = new Product();

    when(userService.getUserById(customerId)).thenReturn(customer);
    when(productService.getProductById(productId)).thenReturn(product);
    when(reviewRepository.existsByProductIdAndCustomerId(productId, customerId)).thenReturn(false);

    reviewService.createReview(request, productId, customerId);

    ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
    verify(reviewRepository).save(captor.capture());

    Review savedReview = captor.getValue();
    assertEquals(request.getContent(), savedReview.getContent());
    assertEquals(request.getRating(), savedReview.getRating());
    assertEquals(product, savedReview.getProduct());
    assertEquals(customer, savedReview.getCustomer());
  }

  @Test
  void createReview_reviewAlreadyExists_shouldThrowException() {
    UUID productId = UUID.randomUUID();
    UUID customerId = UUID.randomUUID();
    CreateReviewRequest request = new CreateReviewRequest();
    request.setContent("Great product");
    request.setRating(5);

    Customer customer = new Customer();
    Product product = new Product();

    when(userService.getUserById(customerId)).thenReturn(customer);
    when(productService.getProductById(productId)).thenReturn(product);
    when(reviewRepository.existsByProductIdAndCustomerId(productId, customerId)).thenReturn(true);

    AppException exception =
        assertThrows(
            AppException.class, () -> reviewService.createReview(request, productId, customerId));

    assertEquals(ErrorCode.REVIEW_EXISTS, exception.getErrorCode());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    verify(reviewRepository, never()).save(any());
  }

  @Test
  void getReviewsByProduct_validProduct_shouldReturnReviews() {
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setId(productId);

    List<ReviewWithUserPreviewProjection> mockReviews =
        List.of(mock(ReviewWithUserPreviewProjection.class));

    when(productService.getProductById(productId)).thenReturn(product);
    when(reviewRepository.findByProductId(productId)).thenReturn(mockReviews);

    List<ReviewWithUserPreviewProjection> result = reviewService.getReviewsByProduct(productId);

    assertEquals(mockReviews, result);
    verify(productService).getProductById(productId);
    verify(reviewRepository).findByProductId(productId);
  }
}
