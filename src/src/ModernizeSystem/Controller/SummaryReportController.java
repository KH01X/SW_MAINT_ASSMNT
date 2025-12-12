package ModernizeSystem.Controller;

import ModernizeSystem.Model.Game;
import ModernizeSystem.Model.SalesSummaryModel;
import ModernizeSystem.View.SummaryReportView;
import ModernizeSystem.FileIOService;

import java.util.List;

public class SummaryReportController {

    private final SummaryReportView view;

    public SummaryReportController() {
        this.view = new SummaryReportView();
    }

    public void generateReport(int[] quantities) {

        // Read games from file
        List<Game> gameList = FileIOService.readGameData();

        // Model
        SalesSummaryModel model = new SalesSummaryModel(gameList, quantities);

        // Display using view
        view.displaySummary(model);
    }
}
