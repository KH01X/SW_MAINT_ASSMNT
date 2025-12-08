package ModernizeSystem;

/**
 * Domain-friendly response returned by {@link RegistrationService}.
 */
public record RegistrationResult(boolean success, String message, Customer customer) {
}
