package ModernizeSystem.Service; // MUST be in the Service package

import ModernizeSystem.Model.Review;
import ModernizeSystem.Repository.ReviewRepository;
import java.util.List;

/**
 * Service layer responsible for Review business logic and data coordination (SRP: Business Logic).
 * It coordinates with the ReviewRepository to fulfill data requests.
 */
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService() {
        // Initialize dependency (Calls the Repository layer)
        this.reviewRepository = new ReviewRepository();
    }

    /**
     * Retrieves all reviews for a specific game, handling the business coordination.
     * @param gameID The ID of the game whose reviews are requested.
     * @return List of Review objects.
     */
    public List<Review> getReviewsForGame(String gameID) {
        return reviewRepository.findByGameId(gameID);
    }
}
