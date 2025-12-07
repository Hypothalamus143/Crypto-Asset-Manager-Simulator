public class AssetFactory {
    private static AssetFactory instance;
    private AssetFactory() {
        AssetRegistry.register(BitcoinMetadata.getInstance());
        AssetRegistry.register(EthereumMetadata.getInstance());
        AssetRegistry.register(SolanaMetadata.getInstance());
    }

    public static AssetFactory getInstance() {
        if(instance == null) {
            instance = new AssetFactory();
        }
        return instance;
    }
    public Asset createAsset(String symbol, double buyPrice, double amount) {
        // Loop through registered metadata
        for (AssetMetadata metadata : AssetRegistry.getAll()) {
            if (metadata.getSymbol().equalsIgnoreCase(symbol.trim())) {
                // Create asset using metadata
                return new Asset(metadata, buyPrice, amount);
            }
        }
        return null; // Symbol not found in registry
    }
}
