package ModernizeSystem;

import java.util.Objects;

public class LoginService {

    private final AuthenticationService authenticationService;

    public LoginService(AuthenticationService authenticationService) {
        this.authenticationService = Objects.requireNonNull(authenticationService);
    }

    public User authenticate(String id, String password) {
        if (id == null || id.isBlank()) {
            throw new SecurityException(ErrorMessage.INVALID_ID);
        }
        if (password == null || password.isBlank()) {
            throw new SecurityException(ErrorMessage.WRONG_PASSWORD);
        }

        return authenticationService.login(id.trim(), password);
    }
}
