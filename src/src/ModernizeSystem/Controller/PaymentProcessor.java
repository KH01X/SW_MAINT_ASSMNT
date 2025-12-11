package ModernizeSystem.Controller;

import ModernizeSystem.Cart;
import ModernizeSystem.Model.AccountWallet;
import ModernizeSystem.Model.Credit;
import ModernizeSystem.Model.Order;

import java.util.ArrayList;

/**
 * Service class responsible for executing the payment transaction logic and generating the final receipt.
 * Purpose: This class acts as a central hub (Service Layer) in the payment domain. It ensures the
 * transaction flow is atomic (all-or-nothing) and adheres to the Dependency Inversion Principle (DIP)
 * by relying only on the IPaymentMethod interface.
 */
public class PaymentProcessor {

    // This method executes payment using ANY object that implements IPaymentMethod.
    /**
     * Executes a payment transaction using the provided payment method and handles the success/failure flow,
     * including generating the final receipt and clearing the cart on success.
     * Purpose: Centralizes the transaction flow, delegates the debit/authorization, and handles final I/O for the transaction completion.
     *
     * @param paymentMethod The concrete object implementing IPaymentMethod (Credit or AccountWallet), used to perform the actual debit/authorization.
     * @param cartList The ArrayList containing the items being purchased, used for printing line items on the receipt and clearing the cart on success.
     * @param order The persistent Order object containing the final subtotal, tax breakdown, and final total, used for the debit amount and receipt display.
     * @param wallet The AccountWallet object, passed as context and used to check/display the remaining balance after a successful transaction.
     * @param credit The Credit object, passed as context and used to check the method type for receipt generation.
     * @return true if the paymentMethod.processPayment call returns true (success), false otherwise, used by the calling view (PaymentView) to determine the next menu state.
     */    public static boolean executePayment(
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
            System.out.println("\nSuccessful Payment! with " + paymentMethod.getMethodName());
            System.out.println("============ RECEIPT ============");
            System.out.println("Game Purchase           Price(RM)");

            for(Cart cartprint : cartList)
            {
                System.out.printf("%-21s%8.2f\n", cartprint.getGameName(), cartprint.getPrice());
            }

            System.out.println("--------------------------------");
            System.out.printf("Subtotal              : %.2f", order.getSubTotal());
            System.out.printf("\nTax(%%5)               : %.2f", order.getTaxPrice());
            System.out.printf("\nTotal Price           : %.2f", total);

            if (paymentMethod.getMethodName().equals("Account Wallet")) {
                System.out.println("\nRemaining Balance     : " + String.format("%.2f", wallet.checkBalance()));
            } else {
                System.out.println("\nYour credit card has been charged.");
            }

            cartList.clear();
            System.out.println("\nTHANK YOU FOR SHOPPING, BYE BYE~");
            return true;
        }
        else
        {
            // FAILURE LOGIC
            System.out.println("\n---------------- FAILED TRANSACTION ----------------");
            if (paymentMethod.getMethodName().equals("Account Wallet")) {
                System.out.println("Insufficient Funds!! Please Top-Up or select another method.");
            } else {
                System.out.println("Credit Card authorization failed.");
            }
            return false;
        }
    }
}
