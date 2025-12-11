/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModernizeSystem.Model;

import ModernizeSystem.Controller.IPaymentMethod;

/**
 * Credit object that will hold the card credit data
 */
public class Credit implements IPaymentMethod {
    private String number;
    private String type;
    private String expDate;

    /**
     * empty constructor
     */
    public Credit()
    {
        
    }

    /**
     * constructor to store the credit card info
     * @param number credit card number
     * @param type credit card's bank name
     * @param expDate the credit card's expiration date
     */
    public Credit (String number, String type, String expDate)
    {
        this.expDate = expDate;
        this.number = number;
        this.type = type;
    }

    /**
     * setter for card number
     * @param number card number
     */
    public void setNumber(String number)
    {
        this.number = number;
    }

    /**
     * setter for bank name
     * @param type bank name
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * setter for card's expiration date
     * @param expDate card's expiration date
     */
    public void setExpDate(String expDate)
    {
        this.expDate = expDate;
    }

    /**
     * to display the card information
     * @return the display with account number, bank name, and expiry date
     */
    @Override
    public String toString()
    {
        return String.format("""
                        \n=====================================================
                             Account Number > %s
                             Account Type   > %s
                             Expiry Date    > %s
                        =====================================================
                            \s""", this.number, this.type, this.expDate);
    }

    /**
     * the method that will actually process the payment using credit card
     * in here we assume that the payment will automatically charge the user's credit card bank
     * @param total total price to be paid
     * @param order order object
     * @param wallet wallet object
     * @param credit credit card object
     * @return true if success where account number not empty, else false
     */
    @Override
    public boolean processPayment(double total, Order order, AccountWallet wallet, Credit credit) {

        return this.number != null && !this.number.isEmpty();
    }

    /**
     *
     * @return return payment method's name
     */
    @Override
    public String getMethodName() {
        return "Credit Card";
    }
    
}
