package ModernizeSystem;

/**
 * MODIFIED AND ADDED
 * Using an enum for the payment choices
 */
public enum PaymentChoice {
    CREDIT_CARD(1),
    ACCOUNT_WALLET(2),
    INVALID(-1); // Default for error handling

    private final int value;

    PaymentChoice(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Helper method to convert an integer input back to an enum constant
    public static PaymentChoice fromValue(int value) {
        for (PaymentChoice choice : PaymentChoice.values()) {
            if (choice.value == value) {
                return choice;
            }
        }
        return INVALID;
    }
}
