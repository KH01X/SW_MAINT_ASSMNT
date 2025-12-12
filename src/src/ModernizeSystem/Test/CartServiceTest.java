package ModernizeSystem.Test;

import ModernizeSystem.Service.CartService;
import ModernizeSystem.Model.Cart;
import ModernizeSystem.Model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test class for CartService.java, ensuring all cart transactional logic
 * and calculations are correctly implemented (Criteria 4 - Testing).
 */
public class CartServiceTest {

    private CartService cartService;
    private List<Cart> mockCartList;
    private Game mockGame1;
    private Game mockGame2;

    @BeforeEach
    void setUp() {
        // Initializes services and mock data before each test method runs
        cartService = new CartService();
        mockCartList = new ArrayList<>();

        // Mock game data (using simple constructor arguments for Game/Cart Model)
        mockGame1 = new Game("G1001", "Night in the Woods", 49.00);
        mockGame2 = new Game("G1002", "Undertale", 23.00);

        // Populate the cart for multi-item tests
        cartService.addItemToCart(mockCartList, mockGame1);
        cartService.addItemToCart(mockCartList, mockGame2);
    }

    // --- Test SRP: Calculation Logic ---
    @Test
    void testCalculateSubTotal_MultipleItems() {
        // Expected: 49.00 + 23.00 = 72.00
        assertEquals(72.00, cartService.calculateSubTotal(mockCartList), 0.001,
                "Subtotal calculation should be the sum of all item prices.");
    }

    @Test
    void testCalculateSubTotal_EmptyCart() {
        List<Cart> emptyCart = new ArrayList<>();
        assertEquals(0.0, cartService.calculateSubTotal(emptyCart),
                "Subtotal of an empty cart should be 0.0.");
    }

    // --- Test SRP: Item Management Logic (Core Added Functionality) ---
    @Test
    void testAddItemToCart() {
        int initialSize = mockCartList.size();
        cartService.addItemToCart(mockCartList, mockGame1);
        assertEquals(initialSize + 1, mockCartList.size(),
                "Cart size should increase by one after adding an item.");
    }

    @Test
    void testRemoveItemFromCart_SuccessfulRemoval() {
        // Remove G1002 (Undertale)
        int initialSize = mockCartList.size();
        assertTrue(cartService.removeItemFromCart(mockCartList, "G1002"),
                "Should return true if an item was successfully removed.");
        assertEquals(initialSize - 1, mockCartList.size(),
                "Cart size must decrease by one after removal.");

        // Verify calculation after removal: Should be 49.00
        assertEquals(49.00, cartService.calculateSubTotal(mockCartList), 0.001,
                "Subtotal must reflect the price change after removal.");
    }

    @Test
    void testRemoveItemFromCart_ItemNotFound() {
        // Attempt to remove non-existent ID (Preventive Test)
        int initialSize = mockCartList.size();
        assertFalse(cartService.removeItemFromCart(mockCartList, "G9999"),
                "Should return false if the item ID was not found.");
        assertEquals(initialSize, mockCartList.size(),
                "Cart size must remain unchanged if removal fails.");
    }

    @Test
    void testClearCart() {
        assertFalse(mockCartList.isEmpty());
        cartService.clearCart(mockCartList);
        assertTrue(mockCartList.isEmpty(), "Cart list must be empty after clearing.");
        assertEquals(0.0, cartService.calculateSubTotal(mockCartList),
                "Subtotal must be zero after clearing the cart.");
    }
}
