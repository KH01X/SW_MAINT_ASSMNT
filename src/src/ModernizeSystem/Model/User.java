/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModernizeSystem.Model;

/**
 *
 * @author Bruh
 */
public class User {
    private String userID;
    private String userPw;
    private String userEmail;
    
    public User(){
        
    }
    
    //Constructor
    public User(String  userID, String userPw, String userEmail){
        this.userID = userID;
        this.userPw = userPw;
        this.userEmail = userEmail;
    }
    
    //Accessor
    public String getuserID(){
      return userID;
    }
    public String getuserPw(){
      return userPw;
    }
    public String getuserEmail(){
      return userEmail;
    }
    //Setter
    public void setuserID(String userID){
        this.userID = userID;
    }
    public void setuserPw(String userPw){
        this.userPw= userPw;
    }
    public void setuserEmail(String userEmail){
        this.userEmail = userEmail;
    }
    
    public boolean validation(String input1, String input2)
    {
        if(userID != input1)
            return false;
        if(userPw != input2)
            return false;
        else
            return true;
        
    }
}
