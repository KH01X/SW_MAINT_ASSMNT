package ModernizeSystem;

/**
 * THERE IS MODIFIED AND ADDED
 * NEW
 */
// This interface is the high-level abstraction (DIP).
// It defines the contract for all payment processing methods.
public interface IPaymentMethod {

    // Core function to attempt the payment transaction.
    // Returns true if successful, false otherwise.
    public boolean processPayment(double amount, Order order, AccountWallet wallet, Credit credit);

    // Utility to get the method name for reporting/display.
    public String getMethodName();
}
