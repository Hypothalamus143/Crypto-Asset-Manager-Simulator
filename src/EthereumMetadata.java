public final class EthereumMetadata extends AssetMetadata {

    private EthereumMetadata() {
        super(
                "ETH",                                  // symbol
                "Ethereum",                             // name
                "Smart contract platform",              // description
                "Smart Contract Platform",              // category
                10.0,                                   // priceChangeRange (-4 to +6)
                -4.0,                                   // priceChangeOffset
                3200.0,                                 // defaultPrice
                3200.0                                  // currentPrice
        );
    }
    public static AssetMetadata getInstance() {
        return new EthereumMetadata();
    }
}