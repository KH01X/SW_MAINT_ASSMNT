package ModernizeSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service dedicated to handling all File Input/Output operations for data persistence.
 * Adheres to Separation of Concerns by isolating the application from the file system.
 * This file replaces the fragmented I/O logic previously found in Main.java.
 */
public class FileIOService {

    private static final Logger LOGGER = Logger.getLogger(FileIOService.class.getName());
    private static final String GAME_FILE = "available_games.txt";
    private static final CustomerRepository CUSTOMER_REPOSITORY = new FileCustomerRepository();

    // --- Customer Data I/O (Required for Authentication Team) ---
    // NOTE: These methods would be fully implemented by the team member responsible for 1.0/2.0

    public static List<Customer> readCustomerData() {
        return new ArrayList<>(CUSTOMER_REPOSITORY.findAll());
    }

    public static boolean writeCustomer(Customer customer) {
        try {
            CUSTOMER_REPOSITORY.save(customer);
            return true;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Unable to persist customer", ex);
            return false;
        }
    }

    // --- Game Data I/O (Required for your 5.0/6.0 Module) ---

    /**
     * Reads all game data from the persistence file into a List of Game objects.
     * Replaces the legacy filereadingGame() method in Main.java.
     * @return A List of Game objects.
     */
    public static List<Game> readGameData() {
        List<Game> gameList = new ArrayList<>();
        File gameFile = new File(GAME_FILE);

        try (Scanner fileread = new Scanner(gameFile)) {
            while (fileread.hasNextLine()) {
                String gameread = fileread.nextLine();
                String[] parts = gameread.split("\\|");

                // Corrective: Ensure there are enough parts and handle parsing errors gracefully (Preventive Maintenance)
                if(parts.length >= 5){
                    try {
                        double price = Double.parseDouble(parts[2].trim());
                        gameList.add(new Game(parts[0].trim(),
                                parts[1].trim(),
                                price,
                                parts[3].trim(),
                                parts[4].trim()));
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Skipping corrupted game record (Price format error): " + gameread, e);
                    }
                }
            }
        } catch(FileNotFoundException e){
            // Logger used instead of System.out.println("The file does not exist :(")
            // Assuming ErrorMessage is used for constants (Teammate's job):
            // LOGGER.log(Level.SEVERE, ErrorMessage.FILE_NOT_FOUND + " (" + GAME_FILE + ")");
            LOGGER.log(Level.SEVERE, "Game data file not found: " + GAME_FILE, e);
        }
        return gameList;
    }

    /**
     * Appends a new game record to the data file. (Staff function - Module 7.0)
     * Replaces the legacy fileWritingGame() method in Main.java.
     * @param game The Game object to write.
     * @return true if write was successful, false otherwise.
     */
    public static boolean writeGame(Game game) {
        try(FileWriter writegame = new FileWriter(GAME_FILE, true)){
            writegame.write(String.format("%s|%s|%.2f|%s|%s\n",
                    game.getGameID(),
                    game.getGameName(),
                    game.getPrice(),
                    game.getGenre(),
                    game.getGameDesc()));
            return true;
        } catch(IOException e){
            // Logger used instead of e.printStackTrace()
            LOGGER.log(Level.SEVERE, "I/O Error during game writing.", e);
            return false;
        }
    }
}
