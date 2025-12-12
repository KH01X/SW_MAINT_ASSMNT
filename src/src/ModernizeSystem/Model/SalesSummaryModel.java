package ModernizeSystem.Model;

import java.util.ArrayList;
import java.util.List;

public class SalesSummaryModel implements ISalesSummaryService {

    private List<Game> gameList;
    private int[] quantities;

    public SalesSummaryModel(List<Game> gameList) {
        this.gameList = gameList;
        this.quantities = initializeQuantities();
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public int[] getQuantities() {
        return quantities;
    }

    public double calculateAmount(int index) {
        return gameList.get(index).getPrice() * quantities[index];
    }

    @Override
    public String summarizeSales(String text) {
        if (text.length() <= 50) return text;
        return text.substring(0, 50);
    }

    // ==============================
    // Original getQuantity logic
    // ==============================
    private int[] initializeQuantities() {
        // NOTE: originally, size = gameList.size() - 1; kept here
        int sizeHolder = gameList.size() - 1;
        int[] quantity = new int[sizeHolder];

        // Example: increment first game for demo
        if (sizeHolder > 0) {
            quantity[0] += 1;
        }

        return quantity;
    }
}
