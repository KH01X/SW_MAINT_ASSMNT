package ModernizeSystem.Model;

/**
 * Customer model representing a CUSTOMER user.
 * Merged from legacy Customer and modern CustomerModel.
 */
public class CustomerModel extends UserModel {

    // Default constructor
    public CustomerModel() {
        super("", "", "", UserRole.CUSTOMER);
    }

    // Main constructor (matches legacy Customer behavior)
    public CustomerModel(String userID, String userPw, String userEmail) {
        super(userID, userPw, userEmail, UserRole.CUSTOMER);
    }
}
