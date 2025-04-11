package rookies.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntityAudit extends BaseEntity {
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  UUID createdBy;

  UUID updatedBy;

  @NotNull
  @Column(name = "is_deleted", columnDefinition = "boolean default false")
  Boolean isDeleted = false;

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * <p>The {@code equals} method implements an equivalence relation on non-null object references:
   *
   * <ul>
   *   <li>It is reflexive: for any non-null reference value {@code x}, {@code x.equals(x)} should
   *       return {@code true}.
   *   <li>It is symmetric: for any non-null reference values {@code x} and {@code y}, {@code
   *       x.equals(y)} should return {@code true} if and only if {@code y.equals(x)} returns {@code
   *       true}.
   *   <li>It is transitive: for any non-null reference values {@code x}, {@code y}, and {@code z},
   *       if {@code x.equals(y)} returns {@code true} and {@code y.equals(z)} returns {@code true},
   *       then {@code x.equals(z)} should return {@code true}.
   *   <li>It is consistent: for any non-null reference values {@code x} and {@code y}, multiple
   *       invocations of {@code x.equals(y)} consistently return {@code true} or {@code false},
   *       provided no information used in {@code equals} comparisons on the objects is modified.
   *   <li>For any non-null reference value {@code x}, {@code x.equals(null)} should return {@code
   *       false}.
   * </ul>
   *
   * <p>The {@code equals} method for class {@code BaseEntityAudit} also implements the following:
   *
   * <ul>
   *   <li>It is consistent with {@link #hashCode()}.
   *   <li>The {@code equals} method does not depend on any invariants which the {@code
   *       BaseEntityAudit} class maintains.
   * </ul>
   *
   * @param o the object to be compared
   * @return {@code true} if the specified object is equal to this {@code BaseEntityAudit}; {@code
   *     false} otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BaseEntityAudit that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(createdBy, that.createdBy)
        && Objects.equals(updatedBy, that.updatedBy)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt)
        && Objects.equals(isDeleted, that.isDeleted);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), createdBy, updatedBy, createdAt, updatedAt, isDeleted);
  }

  @Override
  public String toString() {
    return "BaseEntityAudit{"
        + "id="
        + getId()
        + ", createdBy="
        + createdBy
        + ", updatedBy="
        + updatedBy
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", isDeleted="
        + isDeleted
        + '}';
  }
}
