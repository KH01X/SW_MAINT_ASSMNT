/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Legacy;

/**
 *
 * @author admin
 */
public class AccountWallet {
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
}
