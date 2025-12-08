package ModernizeSystem;

/**
 * Signals that user-provided data violates one or more validation rules.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
