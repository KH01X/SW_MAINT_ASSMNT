package ModernizeSystem.Test;

import ModernizeSystem.Model.Cart;
import ModernizeSystem.Controller.IPaymentMethod;
import ModernizeSystem.Controller.PaymentProcessor;
import ModernizeSystem.Model.AccountWallet;
import ModernizeSystem.Model.Credit;
import ModernizeSystem.Model.Order;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Test the order and payment processes
 */
public class PaymentTest {
    public static class MockPaymentMethod implements IPaymentMethod {
        public boolean shouldSucceed = true;
        public double amountCalled=0.0;

        /**
         * Simulates the authorization and debit process of a real payment gateway
         * by checking the shouldSucceed flag and performing the debit on the AccountWallet if successful.
         *
         * @param total The final calculated price of the order, used to determine the amount to debit.
         * @param order The session's Order object, used as context for the transaction.
         * @param wallet The AccountWallet object, which is debited if the transaction succeeds.
         * @param credit The Credit object, used as context for credit card processing (not modified in this mock).
         * @return true if the mock transaction is set to succeed (authorization granted), false otherwise. This return value is used by the PaymentProcessor to determine the success flow.
         */
        @Override
        public boolean processPayment(double total, Order order, AccountWallet wallet, Credit credit){
            this.amountCalled = total;
            if (shouldSucceed) {
                wallet.decrease(total);
            }
            return shouldSucceed;
        }

        /**
         * Provides a human-readable name for the payment method for logging and receipt generation.
         *
         * @return A string containing the name of the payment method ("Mock Payment"), used for display in the receipt.
         */
        @Override
        public  String getMethodName() {
            return "Mock Payment";
        }
    }

    /**
     * Helper function to create a new, fully calculated Order object given a raw subtotal,
     * centralizing the SRP-compliant calculation steps for tests.
     *
     * @param subTotal The base price of items before tax, used to set the subTotal field in the Order model.
     * @return A fully initialized Order object with accurate subTotal, taxPrice, and total fields calculated and set.
     */
    private Order createTestOrder(double subTotal){
        Order order = new Order();
        order.setSubTotal(subTotal);
        order.calculateTaxAndTotal();
        return order;
    }

    /**
     * Unit test to verify the Order model correctly calculates the 5% tax and final total
     * for a standard, whole-number subtotal, ensuring the SRP refactoring of calculateTaxAndTotal works.
     *
     */
    @Test
    public void testOrder_CorrectTaxAndTotalCalculation(){
        Order order = createTestOrder(100.00);

        assertEquals("Subtotal should be 100.00", 100.00, order.getSubTotal(), 0.001);
        assertEquals("Tax price should be 5.00", 5.00, order.getTaxPrice(), 0.001);
        assertEquals("Total price should be 105.00", 105.00, order.getTotal(), 0.001);
    }

    /**
     * Unit test to ensure the Order model handles the edge case of a zero subtotal,
     * asserting that the tax and final total are correctly zero.
     *
     */
    @Test
    public void testOrder_ZeroSubtotal(){
        Order order = createTestOrder(0.0);
        assertEquals("Tax should be 0.0", 0.0, order.getTaxPrice(), 0.001);
        assertEquals("Total should be 0.0", 0.0, order.getTotal(), 0.001);
    }

    /**
     * Unit test to verify the Order model correctly calculates and handles floating-point precision
     * for a subtotal that results in a two-decimal final price (e.g., $51.45), ensuring accuracy.
     *
     */
    @Test
    public void testOrder_RoundingPrecision() {
        Order order = createTestOrder(49.00);
        assertEquals("Subtotal is correct", 49.00, order.getSubTotal(), 0.001);
        assertEquals("Total should be 51.45", 51.45, order.getTotal(), 0.001);
    }

    private AccountWallet testWallet;

    /**
     * Setup method run before each individual test to initialize a new AccountWallet object
     * with a fixed starting balance ($200.00), ensuring test isolation and a predictable initial state.
     *
     */
    @Before
    public void setUpWallet(){
        testWallet = new AccountWallet(200.00);
    }

    /**
     * Unit test to verify the AccountWallet successfully debits an amount when sufficient funds are available,
     * ensuring the processPayment method works correctly on a 'success' path.
     *
     */
    @Test
    public void testWallet_DebitSuccess(){
        Order mockOrder = createTestOrder(0.0);

        boolean result = testWallet.processPayment(50.00, mockOrder, testWallet, new Credit());

        assertTrue("Debit should succeed", result);
        assertEquals("Balance should be 150.00 after debit", 150.00, testWallet.checkBalance(), 0.001);
    }

    /**
     * Unit test to verify the AccountWallet correctly rejects a debit attempt when funds are insufficient,
     * ensuring the core business logic is maintained (preventing overdraft).
     *
     */
    @Test
    public void testWallet_DebitFailureInsufficientFunds() {
        Order mockOrder = createTestOrder(0.0);

        boolean result = testWallet.processPayment(200.01, mockOrder, testWallet, new Credit());

        assertFalse("Debit should fail due to insufficient funds", result);
        assertEquals("Balance should not change on failure", 200.00, testWallet.checkBalance(), 0.001);
    }

    /**
     * Unit test to verify the increase() method correctly adds a top-up amount to the AccountWallet's balance.
     *
     */
    @Test
    public void testWallet_IncreaseBalance() {
        testWallet.increase(50.00);
        assertEquals("Balance should be 250.00", 250.00, testWallet.checkBalance(), 0.001);
    }

    /**
     * Unit test to verify the decrease() method correctly subtracts an amount from the AccountWallet's balance.
     *
     */
    @Test
    public void testWallet_DecreaseBalance() {
        testWallet.decrease(50.00);
        assertEquals("Balance should be 150.00", 150.00, testWallet.checkBalance(), 0.001);
    }

    /**
     * Unit test to verify the Credit object's processPayment method returns success when card details are present,
     * simulating a successful external authorization.
     *
     */
    @Test
    public void testCredit_ProcessSuccess_CardSaved() {
        Credit card = new Credit("12345678", "Maybank", "12/26");
        Order mockOrder = createTestOrder(10.00);

        boolean result = card.processPayment(10.50, mockOrder, new AccountWallet(), card);
        assertTrue("Credit card should process successfully when details are present", result);
    }

    /**
     * Unit test to verify the Credit object's processPayment method returns failure when required card details are missing,
     * ensuring the basic authorization check is enforced.
     *
     */
    @Test
    public void testCredit_ProcessFailure_NoCardSaved() {
        Credit card = new Credit(); // Card created with null number
        Order mockOrder = createTestOrder(10.00);

        // Failure occurs because the card number is null/empty
        boolean result = card.processPayment(10.50, mockOrder, new AccountWallet(), card);
        assertFalse("Credit card should fail when card number is not set", result);
    }

    private ArrayList<Cart> mockCartList;

    /**
     * Setup method run before each integration test to initialize a new cart list (mockCartList)
     * and populate it with a sample item, providing necessary input for testing the receipt generation logic
     * in the PaymentProcessor.
     *
     */
    @Before
    public void setUpProcessor() {
        mockCartList = new ArrayList<Cart>();
        mockCartList.add(new Cart("G1000", "Night in the Woods", 49.00));
    }

    /**
     * Integration test to verify the entire successful transaction flow through the decoupled components:
     * PaymentProcessor calls the IPaymentMethod mock, debits the wallet correctly, and clears the cart,
     * ensuring the overall architectural design works.
     *
     */
    @Test
    public void testProcessor_IntegrationSuccessFlow() {
        MockPaymentMethod mockMethod = new MockPaymentMethod();
        AccountWallet wallet = new AccountWallet(100.00);
        Order order = createTestOrder(49.00); // Total: 51.45

        boolean success = PaymentProcessor.executePayment(mockMethod, mockCartList, order, wallet, new Credit());

        assertTrue("Payment Processor should report success", success);
        assertEquals("Mock method should have been called with the final total price", 51.45, mockMethod.amountCalled, 0.001);
        assertEquals("Wallet should be debited by the final total", 48.55, wallet.checkBalance(), 0.001);
        assertTrue("Cart list should be cleared on success", mockCartList.isEmpty());
    }

    /**
     * Integration test to verify the system's behavior when a transaction fails (e.g., authorization fails),
     * asserting that the wallet is not debited and the cart contents are preserved for re-attempt,
     * ensuring atomicity (all or nothing) on failure.
     *
     */
    @Test
    public void testProcessor_IntegrationFailureFlow() {
        MockPaymentMethod mockMethod = new MockPaymentMethod();
        mockMethod.shouldSucceed = false; // Force payment to fail
        AccountWallet wallet = new AccountWallet(100.00);
        Order order = createTestOrder(49.00); // Total: 51.45

        boolean success = PaymentProcessor.executePayment(mockMethod, mockCartList, order, wallet, new Credit());

        assertFalse("Payment Processor should report failure", success);
        assertEquals("Wallet balance should remain unchanged on failure", 100.00, wallet.checkBalance(), 0.001);
        assertFalse("Cart list should NOT be cleared on failure", mockCartList.isEmpty());
    }

}
