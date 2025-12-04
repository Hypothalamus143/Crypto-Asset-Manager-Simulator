public class AssetFactory {
    private static AssetFactory instance;
    private AssetFactory() {}

    public static AssetFactory getInstance() {
        if(instance == null) {
            instance = new AssetFactory();
        }
        return instance;
    }
    public Asset createAsset(String symbol, double buyPrice, double amount) {
        switch (symbol.toUpperCase()) {
            case "BTC":
                return new Bitcoin(buyPrice, amount);
            case "ETH":
                return new Ethereum(buyPrice, amount);
            case "SOL":
                return new Solana(buyPrice, amount);
            default:
                return null;
        }
    }
}
