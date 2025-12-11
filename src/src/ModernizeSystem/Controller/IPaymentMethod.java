package ModernizeSystem.Controller;

import ModernizeSystem.Model.AccountWallet;
import ModernizeSystem.Model.Credit;
import ModernizeSystem.Model.Order;

/**
 * This interface is the high-level abstraction (DIP).
 * It defines the contract for all payment processing methods.
 */
public interface IPaymentMethod {

    /**
     * Core function to attempt the payment transaction.
     *
     * @param amount the total price to be paid
     * @param order order object
     * @param wallet wallet object if user choose this will go to wallet
     * @param credit credit card object
     * @return Returns true if successful, false otherwise.
     */
    public boolean processPayment(double amount, Order order, AccountWallet wallet, Credit credit);

    /**
     * Utility to get the method name for reporting/display.
     * @return method name
     */
    public String getMethodName();
}
