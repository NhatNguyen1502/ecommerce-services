package rookies.ecommerce.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.dto.response.user.CustomerSummaryResponse;
import rookies.ecommerce.entity.user.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
  Optional<Customer> findByIdAndIsDeletedFalse(UUID id);

  Page<CustomerSummaryResponse> findAllByIsDeletedFalse(Pageable pageable);
}
