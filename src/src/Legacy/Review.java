/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Legacy;

/**
 *
 * @author admin
 */
public class Review{
    private int reviewID;
    private String userID;
    private String reviewDesc;
    
    private static int lastReviewID = 1001;
    
    //Function
    public String displayReview()
    {
        return String.format("User ID: " + this.userID 
                           + "\nReview #" + this.reviewID + ": " + this.reviewDesc
                           + "\n" );
    }
    
    //Constructor
    public Review (String userID, String reviewDesc)
    {
        this.reviewID = lastReviewID;
        this.userID = userID;
        this.reviewDesc = reviewDesc;
        
        lastReviewID++;
    }
    
    //Accessor
    public int getReviewID()
    {
        return reviewID;
    }
    public String getUserID()
    {
        return userID;
    }
    public String getReviewDesc()
    {
        return reviewDesc;
    }
    
    //Mutator
    public void setReviewID(int newReviewID)
    {
        this.reviewID = newReviewID;
    }
    public void setUserID(String newUserID)
    {
        this.userID = newUserID;
    }
    public void setReviewDesc(String newReviewDesc)
    {
        this.reviewDesc = newReviewDesc;
    }
}
