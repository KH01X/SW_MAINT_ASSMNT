package ModernizeSystem.Model;

public abstract class UserModel {

    protected String id;
    protected String password;
    protected String email;
    protected UserRole role;

    public UserModel(String id, String password, String email, UserRole role) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
}
