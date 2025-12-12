package ModernizeSystem.Service;

import ModernizeSystem.Model.Game;

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
 * Service dedicated to handling File I/O for Game persistence.
 * Customer persistence is handled by FileCustomerRepository.
 */
public class FileIOService {

    private static final Logger LOGGER =
            Logger.getLogger(FileIOService.class.getName());

    private static final String GAME_FILE = "available_games.txt";

    // =========================================================================
    // GAME DATA I/O
    // =========================================================================

    /**
     * Reads all game data from file.
     */
    public static List<Game> readGameData() {
        List<Game> gameList = new ArrayList<>();
        File gameFile = new File(GAME_FILE);

        try (Scanner fileread = new Scanner(gameFile)) {
            while (fileread.hasNextLine()) {
                String line = fileread.nextLine();
                String[] parts = line.split("\\|");

                if (parts.length >= 5) {
                    try {
                        double price = Double.parseDouble(parts[2].trim());

                        gameList.add(new Game(
                                parts[0].trim(),
                                parts[1].trim(),
                                price,
                                parts[3].trim(),
                                parts[4].trim()
                        ));
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING,
                                "Skipping corrupted game record: " + line, e);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE,
                    "Game data file not found: " + GAME_FILE, e);
        }

        return gameList;
    }

    /**
     * Appends a new game record to the file.
     */
    public static boolean writeGame(Game game) {
        try (FileWriter writer = new FileWriter(GAME_FILE, true)) {
            writer.write(String.format("%s|%s|%.2f|%s|%s%n",
                    game.getGameID(),
                    game.getGameName(),
                    game.getPrice(),
                    game.getGenre(),
                    game.getGameDesc()));
            return true;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "I/O Error during game writing.", e);
            return false;
        }
    }
}
