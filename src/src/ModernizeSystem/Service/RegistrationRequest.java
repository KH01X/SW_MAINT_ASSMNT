package ModernizeSystem.Service;

/**
 * Immutable input model for user registration.
 * Contains the raw data typed by the user.
 */
public record RegistrationRequest(String password,
                                  String confirmPassword,
                                  String email) {
}

