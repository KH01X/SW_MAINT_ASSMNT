package ModernizeSystem;

/**
 * MODIFIED AND ADDED
 */

public enum FailedPaymentOption {
    TOP_UP_WALLET(1),
    RETURN_TO_MAIN_MENU(2),
    RETURN_TO_CART_MENU(3),
    RETRY_PAYMENT(4),
    INVALID(-1); // Safety constant for handling errors

    private final int value;

    FailedPaymentOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Converts a user's integer input into the corresponding enum constant.
     * @param value The integer entered by the user.
     * @return The FailedPaymentOption enum constant.
     */
    public static FailedPaymentOption fromValue(int value) {
        for (FailedPaymentOption option : FailedPaymentOption.values()) {
            if (option.value == value) {
                return option;
            }
        }
        return INVALID;
    }
}