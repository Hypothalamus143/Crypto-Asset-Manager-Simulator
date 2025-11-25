public abstract class Asset {
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
}