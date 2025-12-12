package ModernizeSystem.Model;

import java.util.List;

public class SalesSummaryModel {

    private List<Game> gameList;
    private int[] quantities;

    public SalesSummaryModel(List<Game> gameList, int[] quantities) {
        this.gameList = gameList;
        this.quantities = quantities;
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
}
