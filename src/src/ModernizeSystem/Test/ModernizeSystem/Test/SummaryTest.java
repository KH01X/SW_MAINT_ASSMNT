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

import static org.junit.jupiter.api.Assertions.*;

public class SummaryTest {

    private SalesSummaryModel summaryModel;
    private SummaryReportController controller;
    private SummaryReportView view;

    @BeforeEach
    public void setUp() {
        // ===========================
        // Sample games
        // ===========================
        ArrayList<Game> gameList = new ArrayList<>();
        gameList.add(new Game("G1001", "Super Adventure", 59.99, "Adventure", "Exciting adventure game."));
        gameList.add(new Game("G1002", "Space Shooter", 39.99, "Shooter", "Shoot aliens in space!"));

        // ===========================
        // Initialize model
        // ===========================
        summaryModel = new SalesSummaryModel(gameList);

        // Set example sales quantities
        int[] quantities = summaryModel.getQuantities();
        quantities[0] = 5; // Super Adventure sold 5
        quantities[1] = 3; // Space Shooter sold 3

        // ===========================
        // Initialize controller and view
        // ===========================
        controller = new SummaryReportController(summaryModel);
        view = new SummaryReportView();
    }

    @Test
    public void testViewOutput() {
        // Capture console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        view.displaySummary(summaryModel);

        System.setOut(originalOut);
        String output = outputStream.toString();

        // Verify game names, prices, quantities
        assertTrue(output.contains("Super Adventure"));
        assertTrue(output.contains("Space Shooter"));
        assertTrue(output.contains("59.99"));
        assertTrue(output.contains("39.99"));
        assertTrue(output.contains("5"));
        assertTrue(output.contains("3"));
    }

    @Test
    public void testControllerSummary() {
        String summary = controller.getSummary("Daily Sales Report");
        assertNotNull(summary);
        assertTrue(summary.contains("Super Adventure"));
        assertTrue(summary.contains("Space Shooter"));
    }

    @Test
    public void testModelQuantities() {
        int[] quantities = summaryModel.getQuantities();
        assertEquals(5, quantities[0]);
        assertEquals(3, quantities[1]);
    }

    @Test
    public void testCalculateAmount() {
        double amount0 = summaryModel.calculateAmount(0);
        double amount1 = summaryModel.calculateAmount(1);

        assertEquals(59.99 * 5, amount0, 0.001);
        assertEquals(39.99 * 3, amount1, 0.001);
    }
}
