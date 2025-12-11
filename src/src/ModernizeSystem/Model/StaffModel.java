package ModernizeSystem.Model;

public class StaffModel extends UserModel {

    public StaffModel(String id, String password, String email) {
        super(id, password, email, UserRole.STAFF);
    }

    public StaffModel() {
        super("", "", "", UserRole.STAFF);
    }
}
