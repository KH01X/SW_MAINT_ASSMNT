package ModernizeSystem.Service;

import ModernizeSystem.Model.*;
import ModernizeSystem.Repository.*;

import java.util.Objects;

public class AuthenticationService {

    private final CustomerRepository customerRepo;
    private final StaffRepository staffRepo;

    public AuthenticationService(CustomerRepository cRepo, StaffRepository sRepo) {
        this.customerRepo = Objects.requireNonNull(cRepo);
        this.staffRepo = Objects.requireNonNull(sRepo);
    }

    public UserModel login(String id, String password) {

        if (id == null || id.isBlank() || password == null || password.isBlank())
            throw new SecurityException("Invalid ID or Password.");

        // STAFF LOGIN
        if (id.startsWith("S")) {
            return staffRepo.findById(id)
                    .filter(s -> s.getPassword().equals(password))
                    .orElseThrow(() -> new SecurityException("Invalid ID or Password."));
        }

        // CUSTOMER LOGIN
        return customerRepo.findById(id)
                .filter(c -> c.getPassword().equals(password))
                .orElseThrow(() -> new SecurityException("Invalid ID or Password."));
    }
}
