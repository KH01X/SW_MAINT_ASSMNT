package ModernizeSystem.Util;

/**
 * Business rule violation exception for registration/login.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
