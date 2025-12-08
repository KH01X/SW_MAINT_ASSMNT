package ModernizeSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for user login, identification, and validation.
 */
public class AuthenticationService {

    private final CustomerRepository customerRepository;

    public AuthenticationService() {
        this(new FileCustomerRepository());
    }

    public AuthenticationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Attempts to log a user in by checking credentials against stored data.
     * Uses GENERIC FAILURE MESSAGE so users cannot know if ID or password is wrong.
     */
    public User login(String userID, String password) throws SecurityException {

        if (userID == null || userID.isBlank() ||
                password == null || password.isBlank()) {

            throw new SecurityException("Invalid ID or Password.");
        }

        String normalizedId = userID.trim();
        String normalizedPassword = password.trim();

        // STAFF LOGIN
        if (normalizedId.startsWith("S")) {
            List<Staff> staffList = loadStaffData();
            return authenticate(staffList, normalizedId, normalizedPassword);
        }

        // CUSTOMER LOGIN
        else if (normalizedId.startsWith("C")) {
            List<Customer> customerList = customerRepository.findAll();
            return authenticate(customerList, normalizedId, normalizedPassword);
        }

        // INVALID FORMAT — ALWAYS generic error
        else {
            throw new SecurityException("Invalid ID or Password.");
        }
    }

    /**
     * Generic authentication helper using GENERIC failure messages only.
     */
    private <T extends User> User authenticate(List<T> userList,
                                               String userID,
                                               String password) throws SecurityException {

        Optional<T> foundUser = userList.stream()
                .filter(user -> user.getuserID().equalsIgnoreCase(userID))
                .findFirst();

        if (foundUser.isEmpty()) {
            throw new SecurityException("Invalid ID or Password.");
        }

        User user = foundUser.get();

        if (!user.getuserPw().equals(password)) {
            throw new SecurityException("Invalid ID or Password.");
        }

        System.out.println(ErrorMessage.LOGIN_SUCCESS);
        return user;
    }

    /**
     * TEMPORARY: Loads hardcoded staff data to avoid external dependency.
     */
    private List<Staff> loadStaffData() {
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
