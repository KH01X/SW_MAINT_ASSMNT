package ModernizeSystem;

import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Coordinates console input/output for the login and registration module.
 */
public class ConsoleAuthController {

    private static final Logger LOGGER = Logger.getLogger(ConsoleAuthController.class.getName());
    private static final int MAX_ATTEMPTS = 3;

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final Scanner scanner;

    public ConsoleAuthController(LoginService loginService,
                                 RegistrationService registrationService,
                                 Scanner scanner) {
        this.loginService = Objects.requireNonNull(loginService);
        this.registrationService = Objects.requireNonNull(registrationService);
        this.scanner = Objects.requireNonNull(scanner);
    }

    /**
     * Guides the user through the registration workflow with validation feedback.
     */
    public void handleRegistration() {
        System.out.println("Type exit to return to title screen at any prompt.");

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            // =============== 1. ENTER EMAIL FIRST ===============
            String email = prompt("Enter Email > ");
            if (isExitCommand(email)) {
                System.out.println(ErrorMessage.RETURNING_TO_TITLE);
                return;
            }

            // EMPTY EMAIL CHECK
            if (email.isBlank()) {
                System.out.println("Email cannot be empty!");
                System.out.println("Attempt " + attempt + " of " + MAX_ATTEMPTS + ".");
                continue; // restart email input
            }

            // EMAIL FORMAT CHECK
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println("Invalid Email Format! Example: example@mail.com");
                System.out.println("Attempt " + attempt + " of " + MAX_ATTEMPTS + ".");
                continue;
            }

            // DUPLICATE EMAIL CHECK (BEFORE ASKING PASSWORD)
            if (registrationService.emailExists(email)) {
                System.out.println("This email is already registered! Please use another.");
                System.out.println("Attempt " + attempt + " of " + MAX_ATTEMPTS + ".");
                continue;
            }

            // =============== 2. ENTER PASSWORD ===============
            String password = prompt("Enter Password > ");
            if (isExitCommand(password)) {
                System.out.println(ErrorMessage.RETURNING_TO_TITLE);
                return;
            }

            // =============== 3. CONFIRM PASSWORD ===============
            String confirmPassword = prompt("Confirm Password > ");
            if (isExitCommand(confirmPassword)) {
                System.out.println(ErrorMessage.RETURNING_TO_TITLE);
                return;
            }

            // =============== 4. CREATE ACCOUNT ===============
            try {
                RegistrationRequest req =
                        new RegistrationRequest(password, confirmPassword, email);

                RegistrationResult result = registrationService.register(req);

                System.out.println(result.message());
                System.out.println("New Customer ID > " + result.customer().getuserID());
                return;

            } catch (ValidationException ex) {
                System.out.println(ex.getMessage());
            } catch (RuntimeException ex) {
                LOGGER.log(Level.SEVERE, "Unexpected registration error", ex);
                System.out.println(ErrorMessage.IO_ERROR);
                return;
            }

            System.out.println("Please try again. Attempt " + attempt + " of " + MAX_ATTEMPTS + ".");
        }

        System.out.println("Registration aborted after multiple invalid attempts.");
    }


    /**
     * Handles login attempts with limited retries.
     * @return Authenticated user or null if the user exits or exceeds retries.
     */
    public User handleLogin() {
        System.out.println("Type exit to return to title screen at any prompt.");
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String userId = prompt("Enter ID > ");
            if (isExitCommand(userId)) {
                System.out.println(ErrorMessage.RETURNING_TO_TITLE);
                return null;
            }

            String password = prompt("Enter Password > ");
            if (isExitCommand(password)) {
                System.out.println(ErrorMessage.RETURNING_TO_TITLE);
                return null;
            }

            try {
                return loginService.authenticate(userId, password);
            } catch (SecurityException ex) {
                System.out.println(ex.getMessage());
            }

            System.out.println("Login failed. Attempt " + attempt + " of " + MAX_ATTEMPTS + ".");
        }

        System.out.println("Too many failed attempts. Returning to title screen.");
        return null;
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private boolean isExitCommand(String input) {
        return input != null && "exit".equalsIgnoreCase(input.trim());
    }
}
