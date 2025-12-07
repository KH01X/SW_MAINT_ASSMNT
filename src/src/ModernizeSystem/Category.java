/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModernizeSystem;

/**
 *
 * @author admin
 */
public class Category{
    private String categoryTag;
    private String categoryID;
    //Constructor
    public Category(String categoryTag, String categoryID)
    {
        this.categoryID = categoryID;
        this.categoryTag = categoryTag;
    }
    //Getter
    public String getCategoryTag()
    {
        return this.categoryTag;
    }
    public String getCategoryID()
    {
        return this.categoryID;
    }
    //Setter
    public void setCategoryTag(String newCategoryTag)
    {
        this.categoryTag = newCategoryTag;
    }
    public void setCategoryID(String newCategoryID)
    {
        this.categoryID = newCategoryID;
    }
}
