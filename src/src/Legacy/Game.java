/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Legacy;

/**
 *
 * @author admin
 */

public class Game {
    protected String gameID;
    protected String gameName;
    protected double price;
    protected String gameGenre;
    protected String gameDesc;
    
    //Functions
    public Game(){
        
    }
    
    //Constructor
    public Game (String gameID, String gameName, double price, String gameGenre, String gameDesc)
    {
        this.gameID = gameID;
        this.gameName = gameName;
        this.price = price;
        this.gameGenre = gameGenre;
        this.gameDesc = gameDesc;
    }
    
    //Accessors
    public String getGameID()
    {
        return gameID;
    }
    public String getGameName()
    {
        return gameName;
    }
    public String getGameDesc()
    {
        return gameDesc;
    }
    public double getPrice()
    {
        return price;
    }
     public String getGenre(){
        return gameGenre;
    }
    
    //Mutators
    public void setGameID(String newGameID)
    {
        this.gameID = newGameID;
    }
    public void setGameName(String newGameName)
    {
        this.gameName = newGameName;
    }
    public void setGameDesc(String newGameDesc)
    {
        this.gameDesc = newGameDesc;
    }
    public void setPrice(double newPrice)
    {
        this.price = newPrice;
    }
    public void setGenre(String gameGenre){
        this.gameGenre = gameGenre;
    }
    public Game(String gameID, String gameName, double price){
        this.gameID = gameID;
        this.gameName = gameName;
        this.price = price;
    }
}
