package rookies.ecommerce.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import rookies.ecommerce.entity.BaseEntityAudit;
import rookies.ecommerce.entity.Role;

@Entity
@Getter
@Setter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntityAudit {
  @Email
  @Column(nullable = false, unique = true)
  String email;

  @Column(nullable = false)
  String password;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  Role role;

  @Column(name = "is_active", columnDefinition = "boolean default true")
  boolean isActive = true;

  String refreshToken;

  public Collection<GrantedAuthority> getAuthorities() {
    return role != null && role.getName().equals("ADMIN")
        ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        : Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(email, that.email)
        && Objects.equals(password, that.password)
        && Objects.equals(role, that.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), email, password, role);
  }
}
