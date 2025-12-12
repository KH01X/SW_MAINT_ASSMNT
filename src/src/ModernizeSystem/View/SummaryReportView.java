package ModernizeSystem.View;

import ModernizeSystem.Game;
import ModernizeSystem.Model.SalesSummaryModel;

public class SummaryReportView {

    public void displaySummary(SalesSummaryModel model) {

        System.out.println("                           Welcome to Summary Report!                        ");
        System.out.println("=============================================================================");
        System.out.println("Games                         Quantity Sold                            Amount");

        for (int i = 0; i < model.getGameList().size(); i++) {
            Game game = model.getGameList().get(i);
            int quantity = model.getQuantities()[i];
            double amount = model.calculateAmount(i);

            System.out.printf("\n%-30s %-20d %.2f%n\n", game.getGameName(), quantity, amount);
        }

        System.out.println("Total :                                                                      ");
        System.out.println("=============================================================================");
        System.out.println("                              Have a nice day!                               ");
    }
}
