package ModernizeSystem.Model; // MUST be in the Model package

/**
 * Represents a customer review for a game (Model).
 */
public class Review {
    private int reviewID;
    private String gameID; // Field to link the review to a game
    private String userID;
    private String reviewDesc;

    private static int lastReviewID = 1001;

    // Constructor (Updated to include gameID)
    public Review (String gameID, String userID, String reviewDesc)
    {
        this.reviewID = lastReviewID;
        this.gameID = gameID;
        this.userID = userID;
        this.reviewDesc = reviewDesc;

        lastReviewID++;
    }

    // Accessors (getters)
    public int getReviewID() {
        return reviewID;
    }
    public String getUserID() {
        return userID;
    }
    public String getReviewDesc() {
        return reviewDesc;
    }
    public String getGameID() { // NEW ACCESSOR
        return gameID;
    }

    // Mutators (setters)
    public void setReviewID(int newReviewID) {
        this.reviewID = newReviewID;
    }
    public void setUserID(String newUserID) {
        this.userID = newUserID;
    }
    public void setReviewDesc(String newReviewDesc) {
        this.reviewDesc = newReviewDesc;
    }
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    // Display method
    public String displayReview() {
        return String.format("User ID: " + this.userID
                + "\nReview #" + this.reviewID + ": " + this.reviewDesc
                + "\n" );
    }
}