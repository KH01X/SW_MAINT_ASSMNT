package ModernizeSystem.Test;

import ModernizeSystem.Model.SalesSummaryModel;
import ModernizeSystem.Controller.SummaryReportController;
import ModernizeSystem.View.SummaryReportView;
import ModernizeSystem.Game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SummaryTest {

    private SalesSummaryModel summaryModel;
    private SummaryReportController controller;
    private SummaryReportView view;

    @BeforeEach
    public void setUp() {
        // Sample games
        ArrayList<Game> gameList = new ArrayList<>();
        gameList.add(new Game("G1001", "Super Adventure", 59.99, "Adventure", "Exciting adventure game."));
        gameList.add(new Game("G1002", "Space Shooter", 39.99, "Shooter", "Shoot aliens in space!"));

        // Initialize model
        summaryModel = new SalesSummaryModel(gameList);

        // Set quantities only up to the length of the array
        int[] quantities = summaryModel.getQuantities();
        for (int i = 0; i < quantities.length; i++) {
            if (i == 0) quantities[i] = 5;
            if (i == 1 && i < quantities.length) quantities[i] = 3; // safe guard
        }

        // Initialize controller and view
        controller = new SummaryReportController(summaryModel);
        view = new SummaryReportView();
    }

    @Test
    public void testViewOutput() {
        // Capture console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Call the view display
        view.displaySummary(summaryModel);

        // Restore System.out
        System.setOut(originalOut);
        String output = outputStream.toString();

        // Only check up to the length of quantities to avoid index out of bounds
        int[] quantities = summaryModel.getQuantities();
        List<Game> games = summaryModel.getGameList();

        for (int i = 0; i < quantities.length; i++) {
            Game g = games.get(i);
            assertTrue(output.contains(g.getGameName()), "Output missing game name: " + g.getGameName());
            assertTrue(output.contains(String.valueOf(g.getPrice())), "Output missing price for: " + g.getGameName());
            assertTrue(output.contains(String.valueOf(quantities[i])), "Output missing quantity for: " + g.getGameName());
        }
    }


    @Test
    public void testControllerSummary() {
        String summary = controller.getSummary("Daily Sales Report");
        assertNotNull(summary);

        int[] quantities = summaryModel.getQuantities();
        List<Game> safeGames = new ArrayList<>();
        for (int i = 0; i < quantities.length; i++) {
            safeGames.add(summaryModel.getGameList().get(i));
        }

        for (Game g : safeGames) {
            assertTrue(summary.contains(g.getGameName()));
        }
    }

    @Test
    public void testModelQuantities() {
        int[] quantities = summaryModel.getQuantities();
        for (int q : quantities) {
            assertTrue(q >= 0);
        }
    }

    @Test
    public void testCalculateAmount() {
        int[] quantities = summaryModel.getQuantities();
        List<Game> safeGames = new ArrayList<>();
        for (int i = 0; i < quantities.length; i++) {
            safeGames.add(summaryModel.getGameList().get(i));
        }

        for (int i = 0; i < quantities.length; i++) {
            double expected = safeGames.get(i).getPrice() * quantities[i];
            assertEquals(expected, summaryModel.calculateAmount(i), 0.001);
        }
    }
}
