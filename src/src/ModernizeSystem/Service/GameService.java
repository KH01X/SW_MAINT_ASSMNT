package ModernizeSystem.Service;

import ModernizeSystem.Model.Game;

import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Service responsible for managing the Game Catalog and related logic
 */
public class GameService {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());

    /**
     * Finds and returns a Game object from the list based on the user's menu selection number.
     * Implements validation logic to ensure selection is within bounds.
     *
     * @param gameList The list of available games.
     * @param menuOption The 1-based index selected by the user.
     * @return The Selected Game object.
     * @throws IndexOutOfBoundsException if the menuOption is outside the list bounds.
     */
    public Game getSelectedGame(List<Game> gameList, int menuOption) throws IndexOutOfBoundsException {
        // Corrective: Change 1-based index to 0-based for list access.
        if (menuOption > 0 && menuOption <= gameList.size()) {
            return gameList.get(menuOption - 1);
        } else {
            // Preventive: Throw specific exception for validation handling in the Main (View) class.
            throw new IndexOutOfBoundsException("Game selection index " + menuOption + " is out of bounds.");
        }
    }

    /**
     * Filters games by a specified genre using the Java Stream API.
     *
     * @param gameList The list of available games.
     * @param genre The genre string to filter by.
     * @return A list of games matching the specified genre (case-insensitive).
     */
    public List<Game> filterGamesByGenre(List<Game> gameList, String genre) {
        return gameList.stream()
                .filter(game -> game.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }
}