package rookies.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rookies.ecommerce.dto.response.user.CustomerSumaryResponse;
import rookies.ecommerce.entity.user.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByIdAndIsDeletedFalse(UUID id);

    Page<CustomerSumaryResponse> findAllByIsDeletedFalse(Pageable pageable);
}
