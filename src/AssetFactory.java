public class AssetFactory {
    private static AssetFactory instance;
    private AssetRegistry assetRegistry;
    private AssetFactory() {
        assetRegistry = AssetRegistry.getInstance();
        assetRegistry.register(BitcoinMetadata.getInstance());
        assetRegistry.register(EthereumMetadata.getInstance());
        assetRegistry.register(SolanaMetadata.getInstance());
    }

    public static AssetFactory getInstance() {
        if(instance == null) {
            instance = new AssetFactory();
        }
        return instance;
    }
    public Asset createAsset(String symbol, double buyPrice, double amount) {
        // Loop through registered metadata
        for (AssetMetadata metadata : assetRegistry.getAll()) {
            if (metadata.getSymbol().equalsIgnoreCase(symbol.trim())) {
                // Create asset using metadata
                return new Asset(metadata, buyPrice, amount);
            }
        }
        return null; // Symbol not found in registry
    }
}
