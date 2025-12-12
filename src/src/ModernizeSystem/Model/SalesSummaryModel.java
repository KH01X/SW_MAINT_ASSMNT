package ModernizeSystem.Model;

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
        int[] quantity = new int[gameList.size()]; // match number of games
        // Example: optionally increment first game for demo
        if (gameList.size() > 0) {
            quantity[0] += 1;
        }
        return quantity;
    }

}
