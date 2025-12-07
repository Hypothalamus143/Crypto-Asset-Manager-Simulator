public final class AssetMetadataBuilder {
    private String symbol;
    private String name;
    private String description = "";
    private String category = "Cryptocurrency";
    private double priceChangeRange = 10.0;
    private double priceChangeOffset = -5.0;
    private double defaultPrice = 100.0;
    private double currentPrice = 100.0;

    public AssetMetadataBuilder symbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public AssetMetadataBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AssetMetadataBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AssetMetadataBuilder category(String category) {
        this.category = category;
        return this;
    }

    public AssetMetadataBuilder priceChangeRange(double priceChangeRange) {
        this.priceChangeRange = priceChangeRange;
        return this;
    }

    public AssetMetadataBuilder priceChangeOffset(double priceChangeOffset) {
        this.priceChangeOffset = priceChangeOffset;
        return this;
    }

    public AssetMetadataBuilder defaultPrice(double defaultPrice) {
        this.defaultPrice = defaultPrice;
        return this;
    }

    public AssetMetadataBuilder currentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        return this;
    }

    public AssetMetadataBuilder priceVolatility(double minChange, double maxChange) {
        this.priceChangeOffset = minChange;
        this.priceChangeRange = maxChange - minChange;
        return this;
    }

    public AssetMetadataBuilder price(double price) {
        this.defaultPrice = price;
        this.currentPrice = price;
        return this;
    }

    public AssetMetadata build() {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalStateException("Symbol is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Name is required");
        }

        return new AssetMetadata(
                symbol.trim().toUpperCase(),
                name.trim(),
                description,
                category,
                priceChangeRange,
                priceChangeOffset,
                defaultPrice,
                currentPrice
        );
    }

    // Convenience static factory method
    public static AssetMetadataBuilder create(String symbol, String name) {
        return new AssetMetadataBuilder()
                .symbol(symbol)
                .name(name);
    }
}