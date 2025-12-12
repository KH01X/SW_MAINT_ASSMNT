package ModernizeSystem.Service;

import ModernizeSystem.Model.Cart;
import ModernizeSystem.Model.Game;

import java.util.List;

/**
 * Service responsible for managing the state and business logic of the Shopping Cart
 * Adheres to SRP by encapsulating all cart manipulation and financial calculation logic.
 */
public class CartService {

    /**
     * Calculates the subtotal price of all items currently in the cart.
     *
     * @param cartList The list of items in the cart.
     * @return The total price of all items before tax/discounts.
     */
    public double calculateSubTotal(List<Cart> cartList) {

        return cartList.stream()
                .mapToDouble(Cart::getPrice)
                .sum();
    }

    /**
     * Adds a game to the customer's cart.
     *
     * @param cartList The current list of cart items.
     * @param game The Game object containing the details of the item to add.
     */
    public void addItemToCart(List<Cart> cartList, Game game) {
        // Creates a new Cart model instance based on the Game's properties.
        cartList.add(new Cart(game.getGameID(), game.getGameName(), game.getPrice()));
    }

    // --- Newly Added Essential Cart Operations ---

    /**
     * Removes the first instance of a game from the cart based on its unique Game ID.
     * This method is essential for complete item management
     * * @param cartList The current list of items in the cart.
     * @param gameId The ID of the game to remove.
     * @return true if an item was successfully removed, false otherwise.
     */

    public boolean removeItemFromCart(List<Cart> cartList, String gameId) {
        // Uses stream to find the first matching item, then removes it using List's built-in remove method.
        return cartList.removeIf(item -> item.getGameID().equalsIgnoreCase(gameId));
    }

    /**
     * Clears all items from the cart.
     * This is required for checkout finalization or session abandonment.
     * * @param cartList The current list of items in the cart.
     */
    public void clearCart(List<Cart> cartList) {
        cartList.clear();
    }
}