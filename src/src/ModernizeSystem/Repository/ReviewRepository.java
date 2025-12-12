package ModernizeSystem.Repository; // MUST be in the Repository package

import ModernizeSystem.Model.Review;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository layer responsible for data access of Review objects (SRP: Persistence).
 * Encapsulates the hardcoded, in-memory legacy review data.
 */
public class ReviewRepository {

    private static final List<Review> ALL_REVIEWS = new ArrayList<>();

    // Static initializer block: Loads the legacy data once (replaces the broken main() setup).
    static {
        // --- Game 1 (Example ID: G1001) ---
        ALL_REVIEWS.add(new Review("G1001", "Markiplier#87", "This game is lowkey amazing!"));
        ALL_REVIEWS.add(new Review("G1001", "AmogusSussy#69", "AMOGUS!"));
        ALL_REVIEWS.add(new Review("G1001", "Cool#420", "I love Mae!"));

        // --- Game 2 (Example ID: G1002) ---
        ALL_REVIEWS.add(new Review("G1002", "AloySUS#24", "Undertail is COOL"));
        ALL_REVIEWS.add(new Review("G1002", "sanslover#16", "I WANNA BANG THAT SKEELTON"));
        ALL_REVIEWS.add(new Review("G1002", "Metaton#420", "RATINGS ARE OFF THE CHARTS!"));

        // --- Game 3 (Example ID: G1003) ---
        ALL_REVIEWS.add(new Review("G1003", "HollowKnightFan#124", "Hollow Knight has been my long time favourite!"));
        ALL_REVIEWS.add(new Review("G1003", "Kevin#****", "ShAW!"));
        ALL_REVIEWS.add(new Review("G1003", "Hornet", "@Kevin#**** that's not funny"));

        // --- Game 4 (Example ID: G1004) ---
        ALL_REVIEWS.add(new Review("G1004", "PewDiePie#bro", "THIS GAME IS TRASH! :trash_bin:"));
        ALL_REVIEWS.add(new Review("G1004", "Alden Ling#132", "this game copied my name!!!"));
        ALL_REVIEWS.add(new Review("G1004", "Eiden Ring#992", "@Alden Ling#132 IKR"));

        // --- Game 5 (Example ID: G1005) ---
        ALL_REVIEWS.add(new Review("G1005", "Markus#sigma", "well, i tried"));
        ALL_REVIEWS.add(new Review("G1005", "Android#182", "I LOVE THIS GAME!"));
        ALL_REVIEWS.add(new Review("G1005", "Markiplier#87", "Hello everybody my name is Markiplier."));
    }

    /**
     * Finds all reviews associated with a specific Game ID (Stream API).
     * @param gameID The ID of the game (e.g., G1001).
     * @return A list of reviews for that game.
     */
    public List<Review> findByGameId(String gameID) {
        return ALL_REVIEWS.stream()
                .filter(review -> review.getGameID().equalsIgnoreCase(gameID))
                .collect(Collectors.toList());
    }
}
