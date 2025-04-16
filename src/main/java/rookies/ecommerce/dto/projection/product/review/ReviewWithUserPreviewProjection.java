package rookies.ecommerce.dto.projection.product.review;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

public interface ReviewWithUserPreviewProjection {
  UUID getId();

  String getContent();

  Integer getRating();

  LocalDateTime getCreatedAt();

  Customer getCustomer();

  interface Customer {
    UUID getId();

    String getEmail();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
  }
}
