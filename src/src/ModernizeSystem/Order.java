/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModernizeSystem;

/**
 * THERE IS MODIFIED AND ADDED
 * @author admin
 */
import java.text.SimpleDateFormat;  
import java.util.Date;  

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
     * MODIFIED/ADDED: New cohesive method for calculation (SRP: Calculation)
     */

    public void calculateTaxAndTotal()
    {
        // Responsibility 1 (Calculation Logic): Determines tax and final total.
        this.taxPrice = this.subTotal * tax / 100.0; // Calculate 5% tax
        this.total = this.subTotal + this.taxPrice;  // Calculate final total
    }

    // REMOVED: The old calculateFinal(double) and calculateTax(double) methods.
    public double getTotal(){
        return total;
    }
    public double getSubTotal(){
        return subTotal;
    }
    public double getTaxPrice(){ // Added accessor for tax price
        return taxPrice;
    }
    //Functions
    
    public String getOrderID()
    {
        this.orderID = "O" + numOrderID;
        numOrderID++;
        return orderID;
    }
    
    public Date getOrderDate()
    {
        return this.orderDate;
    }
    public String getOrderDesc()
    {
        return this.orderDesc;
    }
    
    public void setOrderID(String newOrderID)
    {
        this.orderID = newOrderID;
    }
    public void setOrderDate(Date newOrderDate)
    {
        this.orderDate = newOrderDate;
    }
    public void setOrderDesc(String newOrderDesc)
    {
        this.orderDesc = newOrderDesc;
    }
    public void setSubTotal(double subTotal){
        this.subTotal = subTotal;
    }
    
    @Override
    public String toString()
    {
        this.total = this.subTotal * 1.05;
        
        return String.format("""
                             ====== Your Order ======
                               Order number  > %s
                               Order Date    > %s
                               Order Desc    >
                               Subtotal      > %.2f
                               Tax Rate(5%%)  > %.2f
                               Total         > %.2f
                             """, orderID,orderDate,subTotal, taxPrice, total);
    }
}
