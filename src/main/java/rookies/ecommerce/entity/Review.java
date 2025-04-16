package rookies.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import rookies.ecommerce.entity.user.Customer;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "reviews",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_id", "product_id"})})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  Customer customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  Product product;

  String content;

  @Min(1)
  @Max(5)
  Integer rating;

  @CreationTimestamp LocalDateTime createdAt;

  /**
   * Compares two Review objects for equality.
   *
   * @param o the object to compare to
   * @return true if the two objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Review that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(customer, that.customer)
        && Objects.equals(product, that.product)
        && Objects.equals(content, that.content)
        && Objects.equals(rating, that.rating)
        && Objects.equals(createdAt, that.createdAt);
  }

  /**
   * {@inheritDoc}
   *
   * <p>The hash code for a {@code Review} object is the combination of the hash codes of the {@code
   * customer}, {@code product}, {@code content}, {@code rating} and {@code createdAt} fields.
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), customer, product, content, rating, createdAt);
  }
}
