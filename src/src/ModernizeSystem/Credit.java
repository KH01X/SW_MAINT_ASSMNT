/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModernizeSystem;

/**
 * THERE IS MODIFIED AND ADDED
 * @author admin
 */

public class Credit implements IPaymentMethod {
    private String number;
    private String type;
    private String expDate;
    
    public Credit()
    {
        
    }
    
    public Credit (String number, String type, String expDate)
    {
        this.expDate = expDate;
        this.number = number;
        this.type = type;
    }
    
    public void setNumber(String number)
    {
        this.number = number;
    }
    public void setType(String type)
    {
        this.type = type;
    }
    public void setExpDate(String expDate)
    {
        this.expDate = expDate;
    }
    
    @Override
    public String toString()
    {
        return String.format("""
                        =====================================================
                             Account Number > %s
                             Account Type   > %s
                             Expiry Date    > %s
                        =====================================================
                             """, this.number, this.type, this.expDate);
    }
    
    public boolean authorized(String numberInput, String typeInput, String expDateInput)
    {
        if (!number.equalsIgnoreCase(numberInput)) return false;
        if (!type.equalsIgnoreCase(typeInput)) return false;
        if (!expDate.equalsIgnoreCase(expDateInput)) return false;
        
        else return true;
    }

    // ADDED: Implementing the core payment processing function
    @Override
    public boolean processPayment(double total, Order order, AccountWallet wallet, Credit credit) {
        // In the absence of a real payment gateway, we authorize if the card details
        // have been successfully entered and stored in the object.
        if (this.number != null && !this.number.isEmpty()) {
            // Assume external bank is successfully charged.
            return true;
        }
        return false;
    }

    // ADDED: Implementing the method name accessor
    @Override
    public String getMethodName() {
        return "Credit Card";
    }
    
}
