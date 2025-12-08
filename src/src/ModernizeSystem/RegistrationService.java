package ModernizeSystem;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegistrationService {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final CustomerRepository customerRepository;

    public RegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
    }

    // ✨ ADDED METHOD (Fixes your error)
    public boolean emailExists(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    public RegistrationResult register(RegistrationRequest request) {
        validateRequest(request);
        ensureEmailUnique(request.email());

        Customer customer = new Customer();
        customer.setuserID(customerRepository.nextCustomerId());
        customer.setuserPw(request.password());
        customer.setuserEmail(request.email());

        return new RegistrationResult(true, ErrorMessage.REGISTER_SUCCESS, customerRepository.save(customer));
    }

    private void validateRequest(RegistrationRequest request) {
        if (request == null) {
            throw new ValidationException("Registration data is missing.");
        }
        if (isBlank(request.password()) || isBlank(request.confirmPassword())) {
            throw new ValidationException("Password cannot be empty.");
        }
        if (!request.password().equals(request.confirmPassword())) {
            throw new ValidationException("Password confirmation does not match.");
        }
        if (isBlank(request.email())) {
            throw new ValidationException(ErrorMessage.INVALID_EMAIL_FORMAT);
        }
        if (!EMAIL_REGEX.matcher(request.email()).matches()) {
            throw new ValidationException(ErrorMessage.INVALID_EMAIL_FORMAT);
        }
    }

    private void ensureEmailUnique(String email) {
        customerRepository.findByEmail(email).ifPresent(existing -> {
            throw new ValidationException("Email already registered.");
        });
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
