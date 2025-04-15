package rookies.ecommerce.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class IBaseEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IBaseEntity that)) return false;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "BaseEntity {" + "id = " + id + "}";
  }
}
