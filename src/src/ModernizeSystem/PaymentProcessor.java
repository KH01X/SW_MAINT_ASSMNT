package ModernizeSystem;

import java.util.ArrayList;

/**
 * THERE IS MODIFIED AND ADDED
 */
public class PaymentProcessor {

    // This method executes payment using ANY object that implements IPaymentMethod.
    public static boolean executePayment(
            IPaymentMethod paymentMethod,
            ArrayList<Cart> cartList,
            Order order,
            AccountWallet wallet,
            Credit credit)
    {
        double total = order.getTotal();
        boolean success = paymentMethod.processPayment(total, order, wallet, credit);

        if (success)
        {
            // SUCCESS LOGIC: Receipt Generation (Moved from Main.java)
            System.out.println("\n      Successful Payment! with " + paymentMethod.getMethodName());
            System.out.println("============ RECEIPT ============");
            System.out.println("  Game Purchase           Price  ");

            for(Cart cartprint : cartList)
            {
                System.out.printf("%s      %.2f\n", cartprint.getGameName(), cartprint.getPrice());
            }

            System.out.println("--------------------------");

            if (paymentMethod.getMethodName().equals("Account Wallet")) {
                System.out.println("  Your Change :" + wallet.checkBalance());
            } else {
                System.out.println("  Your bank account has been charged.");
            }

            cartList.clear();
            System.out.println("THANK YOU FOR SHOPPING, BYE BYE~");
            return true;
        }
        else
        {
            // FAILURE LOGIC
            System.out.println("\n--- FAILED TRANSACTION ---");
            if (paymentMethod.getMethodName().equals("Account Wallet")) {
                System.out.println("Insufficient Funds!! Please Top-Up or select another method.");
            } else {
                System.out.println("Credit Card authorization failed.");
            }
            return false;
        }
    }
}
