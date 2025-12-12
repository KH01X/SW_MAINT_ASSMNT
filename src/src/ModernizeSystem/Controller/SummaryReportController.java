package ModernizeSystem.Controller;

import ModernizeSystem.Game;
import ModernizeSystem.Model.SalesSummaryModel;
import ModernizeSystem.View.SummaryReportView;
import ModernizeSystem.FileIOService;

import java.util.List;

import ModernizeSystem.Model.ISalesSummaryService;

public class SummaryReportController {

    private ISalesSummaryService summaryService;

    public SummaryReportController(ISalesSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    public String getSummary(String text) {
        return summaryService.summarizeSales(text);
    }
}
