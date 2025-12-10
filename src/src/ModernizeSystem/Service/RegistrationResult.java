package ModernizeSystem.Service;

import ModernizeSystem.Model.CustomerModel;

/**
 * Represents the output of a successful registration process.
 * Passed back to the ConsoleAuthController for display.
 */
public record RegistrationResult(boolean success,
                                 String message,
                                 CustomerModel customer) {
}
