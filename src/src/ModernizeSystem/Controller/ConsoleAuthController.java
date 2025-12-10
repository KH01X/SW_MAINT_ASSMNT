package ModernizeSystem.Controller;

import ModernizeSystem.Service.*;
import ModernizeSystem.Repository.*;
import ModernizeSystem.Model.*;

import java.util.Scanner;

public class ConsoleAuthController {

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleAuthController() {
        CustomerRepository customerRepo = new FileCustomerRepository();
        StaffRepository staffRepo = new FileStaffRepository();

        AuthenticationService authService = new AuthenticationService(customerRepo, staffRepo);
        this.loginService = new LoginService(authService);
        this.registrationService = new RegistrationService(customerRepo);
    }

    public void handleRegistration() {
        System.out.println("=== Customer Registration ===");

        String email;

        // -------------------------
        // EMAIL INPUT + VALIDATION LOOP
        // -------------------------
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine();

            if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                break; // valid email → move on
            }

            System.out.println("\nInvalid email format!");
            System.out.println("1. Re-enter Email");
            System.out.println("2. Return to Menu");
            System.out.print("Enter choice > ");

            String choice = scanner.nextLine();

            if (choice.equals("2")) {
                System.out.println("Returning to menu...\n");
                return; // stop registration
            }

            // If choice != 2, loop again to re-enter email
        }

        // -------------------------
        // PASSWORD INPUT
        // -------------------------
        System.out.print("Password: ");
        String pw = scanner.nextLine();

        System.out.print("Confirm Password: ");
        String confirm = scanner.nextLine();

        RegistrationRequest request = new RegistrationRequest(pw, confirm, email);

        // -------------------------
        // ATTEMPT REGISTRATION
        // -------------------------
        try {
            var result = registrationService.register(request);

            System.out.println("\n=================================");
            System.out.println(" REGISTRATION SUCCESSFUL!");
            System.out.println(" Generated Customer ID: " + result.customer().getId());
            System.out.println(" Email: " + result.customer().getEmail());
            System.out.println("=================================\n");

        } catch (Exception e) {
            System.out.println("Registration Failed: " + e.getMessage());
        }
    }




    public UserModel handleLogin() {
        System.out.println("=== Login ===");

        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Password: ");
        String pw = scanner.nextLine();

        try {
            UserModel user = loginService.authenticate(id, pw);
            System.out.println("Login Successful!");

            if (user.getRole() == UserRole.STAFF) {
                System.out.println("Logged in as STAFF");
            } else {
                System.out.println("Logged in as CUSTOMER");
            }

            return user;

        } catch (Exception e) {
            System.out.println("Login Failed: " + e.getMessage());
            return null;
        }
    }
}
