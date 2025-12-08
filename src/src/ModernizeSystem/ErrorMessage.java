package ModernizeSystem;

/**
 * Class containing system-wide error and status messages as final constants.
 */

public class ErrorMessage {
    // --- Status Messages ---
    public static final String REGISTER_SUCCESS = "\nRegister successful!";
    public static final String LOGIN_SUCCESS = "Login Success!";
    public static final String RETURNING_TO_TITLE = "Returning to title screen";
    public static final String EXIT_PROGRAM = "Thanks for coming!";

    // --- Error Messages ---
    public static final String FILE_NOT_FOUND = "Error! Data File Not Found!";
    public static final String IO_ERROR = "Error: Failed to read/write to file.";
    public static final String INVALID_EMAIL_FORMAT = "Invalid Email! Please follow email format!";
    public static final String INVALID_ID = "Error! Invalid ID format or ID not found!";
    public static final String WRONG_PASSWORD = "Error ! wrong password";
    public static final String INVALID_CHOICE = "\nError! Invalid Choice. Only Enter number!";
    public static final String ITEM_NOT_FOUND = "Error: Item not found or could not be removed.";
}