/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModernizeSystem;

/**
 * THERE IS MODIFIED AND ADDED
 * @author admin
 */
public class AccountWallet implements IPaymentMethod { //MODIFIED
    protected String walletID;
    protected double walletAmount;
    
    private static int numAccountID = 1001;
    
    public AccountWallet()
    {
        this.walletID = "AW" + numAccountID;
        
        numAccountID++;
    }
    
    public AccountWallet(double walletAmount)
    {
        this.walletID = "AW" + numAccountID;
        this.walletAmount = walletAmount;
        
        numAccountID++;
    }
    
    @Override
    public String toString()
    {
        return String.format
        ("=================================================================="
        + "\n           Welcome to Account Wallet Top-Up!"
        + "\nID      : " + walletID
        + "\nBalance : RM " + walletAmount
        + "\n==================================================================");
    }
    
    public double checkBalance()
    {
        return walletAmount;
    }
    
    public String getID()
    {
        return walletID;
    }
    
    public void setAmount(double amount)
    {
        this.walletAmount = amount;
    }
    
    public void decrease(double amount)
    {
        this.walletAmount -= amount;
    }
    
    public void increase(double amount)
    {
        this.walletAmount += amount;
    }

    // ADDED/MODIFIED: Implementing the core payment processing function
    @Override
    public boolean processPayment(double total, Order order, AccountWallet wallet, Credit credit)
    {
        // Encapsulating the core business rule (Sufficient Funds Check)
        if (wallet.checkBalance() >= total)
        {
            wallet.decrease(total); // Update the state
            return true;
        }
        else
        {
            return false;
        }
    }

    // ADDED: Implementing the method name accessor
    @Override
    public String getMethodName() {
        return "Account Wallet";
    }
}
