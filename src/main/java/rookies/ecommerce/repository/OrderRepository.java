package rookies.ecommerce.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rookies.ecommerce.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {}
