public class AssetFactory {
    private static AssetFactory instance;
    private AssetRegistry assetRegistry;
    private AssetFactory() {
        assetRegistry = AssetRegistry.getInstance();
        try {
            assetRegistry.register(BitcoinMetadata.getInstance());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assetRegistry.register(EthereumMetadata.getInstance());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            assetRegistry.register(SolanaMetadata.getInstance());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
