package ModernizeSystem;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service responsible for user login, identification, and validation.
 */
public class AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());

    // NOTE: In a modern system, this dependency would be injected.
    private final FileIOService fileIOService = new FileIOService();

    /**
     * Attempts to log a user in by checking credentials against stored data.
     * @param userID The ID entered by the user.
     * @param password The password entered by the user.
     * @return The authenticated User object (Staff or Customer).
     * @throws SecurityException if authentication fails (user not found or wrong password).
     */
    public User login(String userID, String password) throws SecurityException {
        // 1. Determine User Type and load relevant data
        if (userID.startsWith("S")) {
            // Staff data is hardcoded (legacy structure, refactor later)
            List<Staff> staffList = loadStaffData();
            return authenticate(staffList, userID, password, "Staff");
        } else if (userID.startsWith("C")) {
            // Customer data is read via I/O Service (SRP)
            List<Customer> customerList = fileIOService.readCustomerData();
            return authenticate(customerList, userID, password, "Customer");
        } else {
            LOGGER.log(Level.WARNING, "Login attempt with invalid ID format: " + userID);
            throw new SecurityException(ErrorMessage.INVALID_ID);
        }
    }

    /**
     * Generic authentication helper to find user and validate password.
     */
    private <T extends User> User authenticate(List<T> userList, String userID, String password, String type) throws SecurityException {
        // Use Stream API to find the user efficiently (Requirement: Use stream to loop)
        Optional<T> foundUser = userList.stream()
                .filter(user -> user.getuserID().equalsIgnoreCase(userID))
                .findFirst();

        if (foundUser.isEmpty()) {
            LOGGER.log(Level.INFO, type + " ID not found: " + userID);
            throw new SecurityException(ErrorMessage.INVALID_ID);
        }

        User user = foundUser.get();
        if (!user.getuserPw().equals(password)) {
            LOGGER.log(Level.WARNING, type + " login failed for ID: " + userID);
            throw new SecurityException(ErrorMessage.WRONG_PASSWORD);
        }

        // Success
        System.out.println(ErrorMessage.LOGIN_SUCCESS);
        return user;
    }

    /**
     * TEMPORARY: Loads hardcoded staff data to avoid external dependency.
     */
    private List<Staff> loadStaffData() {
        // NOTE: Uses List instead of fixed array (Requirement: Use list instead of array [])
        List<Staff> staffList = new ArrayList<>();
        staffList.add(new Staff("S1000", "staff0", "S1000@mail.com"));
        staffList.add(new Staff("S1001", "staff1", "S1001@mail.com"));
        staffList.add(new Staff("S1002", "staff2", "S1002@mail.com"));
        staffList.add(new Staff("S1003", "staff3", "S1003@mail.com"));
        staffList.add(new Staff("S1004", "staff4", "S1004@mail.com"));
        staffList.add(new Staff("S1005", "staff5", "S1005@mail.com"));
        staffList.add(new Staff("S1006", "staff6", "S1006@mail.com"));
        staffList.add(new Staff("S1007", "staff7", "S1007@mail.com"));
        staffList.add(new Staff("S1008", "staff8", "S1008@mail.com"));
        staffList.add(new Staff("S1009", "staff9", "S1009@mail.com"));
        return staffList;


    }
}