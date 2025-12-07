import java.io.*;
import java.util.*;

public class MarketManager {
    private final String MARKET_FILE = "data/market_prices.csv";
    private static MarketManager instance;

    private final int MAX_PRICES = 100;

    private MarketManager(){
    }

    // PUBLIC STATIC METHODS
    public  void updateMarketPrices() {
       AssetRegistry.updateAllAssetPrices();
    }

    public double getCurrentPrice(String symbol) {
        return AssetRegistry.getCurrentPrice(symbol);
    }


    public List<Double> getPriceHistory(String symbol) {
        return AssetRegistry.getPriceHistory(symbol);
    }

    public String getCryptoName(String symbol) {
        return AssetRegistry.getName(symbol);
    }
    public static MarketManager getInstance(){
        if(instance == null)
            return new MarketManager();
        return instance;
    }
    public int getHistorySize(String symbol) {
        return AssetRegistry.getPriceHistory(symbol).size();
    }

    public Map<String, Double> getAllPrices() {
        return AssetRegistry.getAllCurrentPrices();
    }
}