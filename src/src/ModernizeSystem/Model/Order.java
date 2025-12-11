package ModernizeSystem.Model;

import java.text.SimpleDateFormat;  
import java.util.Date;

/**
 * Order object that will handle the calculation of tax price and total price
 * that will be use when user want to make payment
 */
public class Order {
    protected String orderID;
    protected Date orderDate;
    protected String orderDesc;
    
    //calculation
    protected double subTotal;
    protected double total;
    protected double taxPrice;
    
    private static int numOrderID= 1001;
    private static int tax = 5;

    /**
     * order constructor that store the orderID and the date
     */
    public Order()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD");
        Date date = new Date(); 

        this.orderID = "O" + numOrderID;
        this.orderDate = date;
        numOrderID++;
    }

    /**
     * USE FOR CALCULATING THE PRICE IN MAIN
     * calculate the tax price and total
     */

    public void calculateTaxAndTotal()
    {
        this.taxPrice = this.subTotal * tax / 100.0; // Calculate 5% tax
        this.total = this.subTotal + this.taxPrice;  // Calculate final total
    }

    /**
     *
     * @return total price
     */
    public double getTotal(){
        return total;
    }

    /**
     *
     * @return subTotal of the order that user want to check out
     */
    public double getSubTotal(){
        return subTotal;
    }
    public double getTaxPrice(){ // Added accessor for tax price
        return taxPrice;
    }

    /**
     * setter
     * @param subTotal return the subtotal
     */
    public void setSubTotal(double subTotal){
        this.subTotal = subTotal;
    }

    /**
     * toString method to display the order detail
     * @return order detail
     */
    @Override
    public String toString()
    {
        return String.format("""
                             ====== Your Order ======
                               Order number  > %s
                               Order Date    > %s
                               Order Desc    >>>
                               Subtotal      > %.2f
                               Tax Rate(5%%)  > %.2f
                               Total         > %.2f
                             """, orderID,orderDate,subTotal, taxPrice, total);
    }
}
