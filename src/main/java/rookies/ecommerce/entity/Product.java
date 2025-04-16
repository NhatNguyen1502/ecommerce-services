package rookies.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends BaseEntityAudit {
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  @JsonIgnore
  Category category;

  @NotNull
  @Column(name = "name", nullable = false, length = 200)
  String name;

  @Column(name = "description", columnDefinition = "TEXT")
  String description;

  @NotNull
  @Column(name = "image_url", nullable = false)
  String imageUrl;

  @NotNull
  @Min(0)
  @Column(name = "price", nullable = false)
  double price;

  @Min(0)
  @NotNull
  @Column(name = "quantity", nullable = false)
  int quantity;

  @Column(name = "is_featured", columnDefinition = "boolean default false")
  boolean isFeatured;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
  List<Review> reviews;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Product product = (Product) o;
    return Objects.equals(name, product.name)
        && Objects.equals(category, product.category)
        && Objects.equals(description, product.description)
        && Objects.equals(imageUrl, product.imageUrl)
        && Objects.equals(price, product.price)
        && Objects.equals(quantity, product.quantity)
        && Objects.equals(isFeatured, product.isFeatured);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), name, category, description, imageUrl, price, quantity, isFeatured);
  }
}
