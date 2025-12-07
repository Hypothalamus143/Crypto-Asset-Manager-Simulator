public final class BitcoinMetadata extends AssetMetadata {

    private BitcoinMetadata() {
        super(
                "BTC",                                  // symbol
                "Bitcoin",                              // name
                "First decentralized cryptocurrency",   // description
                "Store of Value",                       // category
                8.0,                                    // priceChangeRange (-3 to +5)
                -3.0,                                   // priceChangeOffset
                45000.0,                                // defaultPrice
                45000.0                                 // currentPrice
        );
    }
    public static AssetMetadata getInstance() {
        return new BitcoinMetadata();
    }
}