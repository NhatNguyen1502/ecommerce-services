package rookies.ecommerce.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "admins")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Admin extends User {
}
