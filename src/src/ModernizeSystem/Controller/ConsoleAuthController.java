package ModernizeSystem.Controller;

import ModernizeSystem.Service.*;
import ModernizeSystem.Repository.*;
import ModernizeSystem.Model.*;
import ModernizeSystem.Util.ErrorMessageLoginRegister;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleAuthController {

    private static final Logger LOGGER =
            Logger.getLogger(ConsoleAuthController.class.getName());

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleAuthController() {
        CustomerRepository customerRepo = new FileCustomerRepository();
        StaffRepository staffRepo = new FileStaffRepository();

        AuthenticationService authService =
                new AuthenticationService(customerRepo, staffRepo);

        this.loginService = new LoginService(authService);
        this.registrationService = new RegistrationService(customerRepo);
    }

    // =========================================================================
    // REGISTRATION
    // =========================================================================
    public void handleRegistration() {

        // ===== UI TITLE (NOT LOGGER) =====
        System.out.println(ErrorMessageLoginRegister.REG_TITLE);
        System.out.println();

        String email;

        while (true) {

            // ===== INPUT PROMPT =====
            System.out.print(ErrorMessageLoginRegister.PROMPT_EMAIL);
            email = scanner.nextLine();

            // ❌ INVALID FORMAT
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {

                // LOGGER FIRST
                LOGGER.warning(ErrorMessageLoginRegister.INVALID_EMAIL_FORMAT);

                // MENU (System.out ONLY)
                System.out.println(ErrorMessageLoginRegister.OPTION_REENTER_EMAIL);
                System.out.println(ErrorMessageLoginRegister.OPTION_RETURN_MENU);
                System.out.print(ErrorMessageLoginRegister.PROMPT_CHOICE);

                String choice = scanner.nextLine();
                if (choice.equals("2")) {
                    System.out.println(ErrorMessageLoginRegister.RETURN_MENU);
                    return;
                }

                System.out.println();
                continue;
            }

            // ❌ DUPLICATE EMAIL
            if (registrationService.emailExists(email)) {

                // LOGGER FIRST
                LOGGER.warning(ErrorMessageLoginRegister.DUPLICATE_EMAIL);

                // MENU
                System.out.println(ErrorMessageLoginRegister.OPTION_REENTER_EMAIL);
                System.out.println(ErrorMessageLoginRegister.OPTION_RETURN_MENU);
                System.out.print(ErrorMessageLoginRegister.PROMPT_CHOICE);

                String choice = scanner.nextLine();
                if (choice.equals("2")) {
                    System.out.println(ErrorMessageLoginRegister.RETURN_MENU);
                    return;
                }

                System.out.println();
                continue;
            }

            // ✅ VALID & UNIQUE
            break;
        }

        // ===== PASSWORD INPUT =====
        System.out.print(ErrorMessageLoginRegister.PROMPT_PASSWORD);
        String pw = scanner.nextLine();

        System.out.print(ErrorMessageLoginRegister.PROMPT_CONFIRM_PASSWORD);
        String confirm = scanner.nextLine();

        try {
            var result = registrationService.register(
                    new RegistrationRequest(pw, confirm, email)
            );

            // ===== SUCCESS OUTPUT =====
            System.out.println("=================================");
            System.out.println(ErrorMessageLoginRegister.REGISTER_SUCCESS);
            System.out.println(ErrorMessageLoginRegister.GENERATED_ID + result.customer().getId());
            System.out.println(ErrorMessageLoginRegister.EMAIL_LABEL + result.customer().getEmail());
            System.out.println("=================================");

        } catch (Exception e) {
            LOGGER.severe(ErrorMessageLoginRegister.REGISTER_FAILED);
        }
    }


    // =========================================================================
    // LOGIN
    // =========================================================================
    public UserModel handleLogin() {

        // ===== SYSTEM MESSAGE (LOGGER ONLY) =====
        LOGGER.info(ErrorMessageLoginRegister.LOGIN_TITLE);
        LOGGER.info("Login attempt");
        System.out.println(); // spacing before input

        // =========================
        // INPUT
        // =========================
        System.out.print(ErrorMessageLoginRegister.PROMPT_ID);
        String id = scanner.nextLine();

        System.out.print(ErrorMessageLoginRegister.PROMPT_PASSWORD);
        String pw = scanner.nextLine();

        try {
            UserModel user = loginService.authenticate(id, pw);

            // LOGGER FIRST
            LOGGER.info("Login successful");

            // USER OUTPUT
            System.out.println(ErrorMessageLoginRegister.LOGIN_SUCCESS);

            if (user.getRole() == UserRole.STAFF) {
                System.out.println(ErrorMessageLoginRegister.LOGIN_STAFF);
            } else {
                System.out.println(ErrorMessageLoginRegister.LOGIN_CUSTOMER);
            }

            return user;

        } catch (Exception e) {

            // LOGGER FIRST
            LOGGER.warning("Login failed");

            // USER MESSAGE
            System.out.println(ErrorMessageLoginRegister.INVALID_CREDENTIALS);
            return null;
        }
    }
}