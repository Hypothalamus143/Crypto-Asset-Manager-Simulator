public final class SolanaMetadata extends AssetMetadata {

    private SolanaMetadata() {
        super(
                "SOL",                                  // symbol
                "Solana",                               // name
                "High-performance blockchain",          // description
                "Layer 1 Blockchain",                   // category
                14.0,                                   // priceChangeRange (-6 to +8)
                -6.0,                                   // priceChangeOffset
                102.0,                                  // defaultPrice
                102.0                                   // currentPrice
        );
    }

    public static AssetMetadata getInstance() {
        return new SolanaMetadata();
    }
}