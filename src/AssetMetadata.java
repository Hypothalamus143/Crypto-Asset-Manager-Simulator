import java.util.ArrayList;
import java.util.List;

public class AssetMetadata {
    private final String symbol;
    private final String name;
    private final String description;
    private final String category;
    private final double priceChangeRange;
    private final double priceChangeOffset;
    private final double defaultPrice;
    private double currentPrice;
    private List<Double> priceHistory;  // Make it mutable for CSV loading

    public AssetMetadata(String symbol, String name, String description,
                         String category, double priceChangeRange,
                         double priceChangeOffset, double defaultPrice,
                         double currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.description = description;
        this.category = category;
        this.priceChangeRange = priceChangeRange;
        this.priceChangeOffset = priceChangeOffset;
        this.defaultPrice = defaultPrice;
        this.currentPrice = currentPrice;
        this.priceHistory = new ArrayList<>();
        this.priceHistory.add(currentPrice);
    }

    // Setter for price history (for CSV loading)
    public void setPriceHistory(List<Double> newPriceHistory) {
        if (newPriceHistory == null || newPriceHistory.isEmpty()) {
            throw new IllegalArgumentException("Price history cannot be null or empty");
        }
        this.priceHistory = new ArrayList<>(newPriceHistory);
        this.currentPrice = newPriceHistory.get(newPriceHistory.size() - 1);
    }
    // Getter for price history
    public List<Double> getPriceHistory() {
        return new ArrayList<>(priceHistory);  // Return defensive copy
    }

    // Get price at specific index
    public double getPriceAt(int index) {
        if (index < 0 || index >= priceHistory.size()) {
            throw new IndexOutOfBoundsException("Invalid price history index: " + index);
        }
        return priceHistory.get(index);
    }

    // Get latest price
    public double getLatestPrice() {
        if (priceHistory.isEmpty()) {
            return currentPrice;
        }
        return priceHistory.get(priceHistory.size() - 1);
    }
    // Getter for current price (latest from history)
    public double getCurrentPrice() {
        return priceHistory.isEmpty() ? currentPrice : getLatestPrice();
    }

    // Rest of the getters
    public void updatePriceWithRandomChange() {
        // Calculate random price change based on range and offset
        double randomFactor = Math.random();
        double changePercent = (randomFactor * priceChangeRange) + priceChangeOffset;

        // Apply the change to current price: ((change/100) + 1) * currentPrice
        double newPrice = ((changePercent / 100.0) + 1.0) * currentPrice;
        if(newPrice < 0.01)
            newPrice = 0.01;
        // Update current price and add to history
        this.currentPrice = newPrice;
        this.priceHistory.add(newPrice);
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPriceChangeRange() { return priceChangeRange; }
    public double getPriceChangeOffset() { return priceChangeOffset; }
    public double getDefaultPrice() { return defaultPrice; }
}