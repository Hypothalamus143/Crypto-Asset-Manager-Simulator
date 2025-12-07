import java.io.*;
import java.util.*;

public class MarketManager {
    private final String MARKET_FILE = "data/market_prices.csv";
    private static MarketManager instance;
    private AssetRegistry assetRegistry;
    private final int MAX_PRICES = 100;

    private MarketManager(){
    }

    // PUBLIC STATIC METHODS
    public  void updateMarketPrices() {
        assetRegistry.updateAllAssetPrices();
    }

    public double getCurrentPrice(String symbol) {
        return  assetRegistry.getCurrentPrice(symbol);
    }


    public List<Double> getPriceHistory(String symbol) {
        return  assetRegistry.getPriceHistory(symbol);
    }

    public String getCryptoName(String symbol) {
        return  assetRegistry.getName(symbol);
    }
    public static MarketManager getInstance(){
        if(instance == null)
            return new MarketManager();
        return instance;
    }
    public int getHistorySize(String symbol) {
        return  assetRegistry.getPriceHistory(symbol).size();
    }

    public Map<String, Double> getAllPrices() {
        return  assetRegistry.getAllCurrentPrices();
    }
}