public class Bitcoin extends Asset {
    public Bitcoin(double buyPrice, double amount) {
        super("Bitcoin", "BTC", buyPrice, amount);
    }

    // REMOVE updatePrice() - handled by MarketManager globally
}