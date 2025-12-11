package ModernizeSystem.Model;

import ModernizeSystem.Controller.IPaymentMethod;

/**
 * Represents the customer's internal wallet model. It is responsible for managing its own financial state
 * (balance) and implementing the IPaymentMethod contract for payment processing.
 *
 */
public class AccountWallet implements IPaymentMethod { //MODIFIED
    protected String walletID;
    protected double walletAmount;
    
    private static int numAccountID = 1001;

    /**
     * Constructor for the AccountWallet class.
     * Purpose: Initializes a new wallet with an auto-generated ID (e.g., AW1001) and a starting balance of 0.0.
     *
     */
    public AccountWallet()
    {
        this.walletID = "AW" + numAccountID;
        
        numAccountID++;
    }

    /**
     * Constructor for the AccountWallet class.
     * Purpose: Initializes a new wallet with an auto-generated ID and a specified starting balance.
     *
     * @param walletAmount The initial balance of the wallet, used to set the starting walletAmount field.
     */
    public AccountWallet(double walletAmount)
    {
        this.walletID = "AW" + numAccountID;
        this.walletAmount = walletAmount;
        
        numAccountID++;
    }

    /**
     * Overrides the default toString method.
     * Purpose: Formats the wallet ID and current balance into a structured, human-readable top-up interface string.
     *
     * @return A formatted string displaying the wallet's welcome message, ID, and current balance.
     */
    @Override
    public String toString()
    {
        return String.format
        ("\n=================================================================="
        + "\n               Welcome to Account Wallet Top-Up!"
        + "\n\nID      : " + walletID
        + "\nBalance : RM " + walletAmount
        + "\n==================================================================");
    }

    /**
     * Accessor method for the wallet balance.
     * Purpose: Retrieves the current monetary balance of the wallet.
     *
     * @return The current value of the walletAmount field (a double), used for balance checks.
     */
    public double checkBalance()
    {
        return walletAmount;
    }

    /**
     * Mutator method to decrease the wallet balance.
     * Purpose: Subtracts the total transaction price from the current wallet balance.
     *
     * @param totalPrice The amount to be subtracted, used to update the internal walletAmount field.
     */
    public void decrease(double totalPrice)
    {
        this.walletAmount -= totalPrice;
    }

    /**
     * Mutator method to increase the wallet balance.
     * Purpose: Adds a specified top-up amount to the current wallet balance.
     *
     * @param amount The amount to be added (the top-up amount), used to update the internal walletAmount field.
     */
    public void increase(double amount)
    {
        this.walletAmount += amount;
    }

    /**
     * Implements the core payment processing logic defined by the IPaymentMethod interface.
     * Purpose: Checks for sufficient funds and performs the debit operation on the wallet if funds are available.
     *
     * @param total The total price of the order, used to check against the current wallet balance.
     * @param order The Order object (context, not used by wallet itself).
     * @param wallet The AccountWallet object itself, used to call its internal checkBalance and decrease methods.
     * @param credit The Credit object (context, not used by wallet itself).
     * @return true if the balance is sufficient and the debit is performed; false if funds are insufficient, used by the PaymentProcessor to determine transaction success.
     */
    @Override
    public boolean processPayment(double total, Order order, AccountWallet wallet, Credit credit)
    {
        if (wallet.checkBalance() >= total)
        {
            wallet.decrease(total);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Implements the accessor for the payment method's name.
     * Purpose: Provides the name of the payment method for receipt generation and logging.
     *
     * @return A string literal "Account Wallet", used for display in the PaymentProcessor receipt.
     */
    @Override
    public String getMethodName() {
        return "Account Wallet";
    }
}
