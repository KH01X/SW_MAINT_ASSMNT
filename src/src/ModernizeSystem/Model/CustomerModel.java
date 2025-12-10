package ModernizeSystem.Model;

public class CustomerModel extends UserModel {

    public CustomerModel(String id, String password, String email) {
        super(id, password, email, UserRole.CUSTOMER);
    }

    public CustomerModel() {
        super("", "", "", UserRole.CUSTOMER);
    }
}
