package ModernizeSystem.Service;

import ModernizeSystem.Model.UserModel;
import ModernizeSystem.Util.ErrorMessageLoginRegister;

public class LoginService {

    private final AuthenticationService authenticationService;

    public LoginService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public UserModel authenticate(String id, String pw) {

        if (id == null || id.isBlank())
            throw new SecurityException(ErrorMessageLoginRegister.INVALID_ID);

        if (pw == null || pw.isBlank())
            throw new SecurityException(ErrorMessageLoginRegister.INVALID_PASSWORD);

        return authenticationService.login(id.trim(), pw.trim());
    }
}
