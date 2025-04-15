package rookies.ecommerce.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "customers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer extends User {

    @Column(length = 50)
    String firstName;

    @Column(length = 50)
    String lastName;

    @Column(nullable = false)
    String phoneNumber;

    @Column(nullable = false)
    String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), phoneNumber, address);
    }
}
