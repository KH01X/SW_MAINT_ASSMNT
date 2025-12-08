package ModernizeSystem;

/**
 * Immutable input collected from the console before passing it to the domain
 * service layer.
 */
public record RegistrationRequest(String password, String confirmPassword, String email) {
}
