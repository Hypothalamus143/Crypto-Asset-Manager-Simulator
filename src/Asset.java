import java.util.Collections;
import java.util.Comparator;

public abstract class Asset implements Comparable<Asset>{
    protected String name;
    protected String symbol;
    protected double buyPrice;  // Instance-specific
    protected double amount;    // Instance-specific
    // REMOVED: currentPrice (will use MarketManager)

    public Asset(String name, String symbol, double buyPrice, double amount) {
        this.name = name;
        this.symbol = symbol;
        this.buyPrice = buyPrice;
        this.amount = amount;
    }

    // Getters
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public double getBuyPrice() { return buyPrice; }
    public double getAmount() { return amount; }

    // Current price comes from MarketManager (class-level)
    public double getCurrentPrice() {
        return MarketManager.getCurrentPrice(symbol);
    }

    // Setters
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }
    public void setAmount(double amount) { this.amount = amount; }

    // Remove updatePrice() - MarketManager handles this globally
    // public abstract void updatePrice(); // DELETE THIS

    // Common methods
    public double getTotalValue() {
        return getCurrentPrice() * amount; // Uses current market price
    }

    public double getUnrealizedProfit() {
        return (getCurrentPrice() - buyPrice) * amount; // Uses current market price
    }
    @Override
    public int compareTo(Asset other) {
        return this.symbol.compareTo(other.symbol);
    }

    // Simple Comparator classes with direction in constructor
    public static class SymbolComparator implements Comparator<Asset> {
        public final boolean ascending;

        public SymbolComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Asset a1, Asset a2) {
            int result = a1.symbol.compareTo(a2.symbol);
            return ascending ? result : -result;
        }
    }

    public static class TotalValueComparator implements Comparator<Asset> {
        public final boolean ascending;

        public TotalValueComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Asset a1, Asset a2) {
            int result = Double.compare(a1.getTotalValue(), a2.getTotalValue());
            if (result == 0) {
                return a1.symbol.compareTo(a2.symbol); // Return immediately for ties
            }
            return ascending ? result : -result;
        }
    }

    public static class ProfitAmountComparator implements Comparator<Asset> {
        public final boolean ascending;

        public ProfitAmountComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Asset a1, Asset a2) {
            int result = Double.compare(a1.getUnrealizedProfit(), a2.getUnrealizedProfit());
            if (result == 0) {
                return a1.symbol.compareTo(a2.symbol); // Return immediately for ties
            }
            return ascending ? result : -result;
        }
    }

    public static class ProfitPercentComparator implements Comparator<Asset> {
        public final boolean ascending;

        public ProfitPercentComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Asset a1, Asset a2) {
            double percent1 = (a1.getCurrentPrice() - a1.getBuyPrice()) / a1.getBuyPrice();
            double percent2 = (a2.getCurrentPrice() - a2.getBuyPrice()) / a2.getBuyPrice();
            int result = Double.compare(percent1, percent2);
            if (result == 0) {
                return a1.symbol.compareTo(a2.symbol); // Return immediately for ties
            }
            return ascending ? result : -result;
        }
    }

    public static class BuyPriceComparator implements Comparator<Asset> {
        public final boolean ascending;

        public BuyPriceComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Asset a1, Asset a2) {
            int result = Double.compare(a1.getBuyPrice(), a2.getBuyPrice());
            if (result == 0) {
                return a1.symbol.compareTo(a2.symbol); // Return immediately for ties
            }
            return ascending ? result : -result;
        }
    }

    public static class AmountComparator implements Comparator<Asset> {
        public final boolean ascending;

        public AmountComparator(boolean ascending) {
            this.ascending = ascending;
        }

        @Override
        public int compare(Asset a1, Asset a2) {
            int result = Double.compare(a1.getAmount(), a2.getAmount());
            if (result == 0) {
                return a1.symbol.compareTo(a2.symbol); // Return immediately for ties
            }
            return ascending ? result : -result;
        }
    }
}