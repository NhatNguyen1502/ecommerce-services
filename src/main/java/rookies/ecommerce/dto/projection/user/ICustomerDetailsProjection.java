package rookies.ecommerce.dto.projection.user;

public interface ICustomerDetailsProjection {
    String getFirstName();
    String getLastName();
    String getPhoneNumber();
    String getAddress();
    boolean isActive();
    String getRoleName();
    String getUserEmail();

    interface User {
        String getEmail();

        interface Role {
            String getName();
        }
    }
}
