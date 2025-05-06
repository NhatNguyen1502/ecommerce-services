package rookies.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntityAudit {
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  Order order;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  Product product;

  @NotNull
  @Min(1)
  @Column(name = "quantity", nullable = false)
  int quantity;

  @NotNull
  @Min(0)
  @Column(name = "price", nullable = false)
  double price;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OrderItem orderItem = (OrderItem) o;
    return quantity == orderItem.quantity
        && Double.compare(orderItem.price, price) == 0
        && Objects.equals(order, orderItem.order)
        && Objects.equals(product, orderItem.product);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), order, product, quantity, price);
  }
}
