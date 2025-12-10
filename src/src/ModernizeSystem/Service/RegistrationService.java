package ModernizeSystem.Service;

import ModernizeSystem.Model.CustomerModel;
import ModernizeSystem.Repository.CustomerRepository;
import ModernizeSystem.Util.*;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegistrationService {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private final CustomerRepository repo;

    public RegistrationService(CustomerRepository repo) {
        this.repo = Objects.requireNonNull(repo);
    }

    public RegistrationResult register(RegistrationRequest req) {

        validate(req);
        ensureUnique(req.email());

        String id = repo.nextCustomerId();

        CustomerModel customer = new CustomerModel(id, req.password(), req.email());
        repo.save(customer);

        return new RegistrationResult(true,
                ErrorMessageLoginRegister.REGISTER_SUCCESS,
                customer);
    }

    private void validate(RegistrationRequest r) {
        if (r == null) throw new ValidationException("Missing data.");
        if (!EMAIL_REGEX.matcher(r.email()).matches())
            throw new ValidationException(ErrorMessageLoginRegister.INVALID_EMAIL_FORMAT);
        if (!r.password().equals(r.confirmPassword()))
            throw new ValidationException("Passwords do not match.");
    }

    private void ensureUnique(String email) {
        repo.findByEmail(email).ifPresent(u -> {
            throw new ValidationException(ErrorMessageLoginRegister.DUPLICATE_EMAIL);
        });
    }
}
