import java.io.*;
import java.util.*;

public class MarketManager {
    private static final String MARKET_FILE = "data/market_prices.csv";
    private static final Map<String, Double> currentPrices = new HashMap<>();

    static {
        loadMarketPrices();
    }

    private MarketManager() {}

    private static void loadMarketPrices() {
        File marketFile = new File(MARKET_FILE);

        // Create directory and default file if it doesn't exist
        if (!marketFile.exists()) {
            createDefaultMarketFile();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(marketFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String symbol = parts[1].trim().toUpperCase();
                    double currentPrice = Double.parseDouble(parts[2].trim());
                    currentPrices.put(symbol, currentPrice);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading market prices: " + e.getMessage());
            initializeDefaultPrices();
        }
    }

    private static void createDefaultMarketFile() {
        new File("data").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MARKET_FILE))) {
            writer.write("asset_type,symbol,current_price");
            writer.newLine();
            writer.write("bitcoin,BTC,45000.00");
            writer.newLine();
            writer.write("ethereum,ETH,3200.00");
            writer.newLine();
            writer.write("solana,SOL,102.00");
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error creating market file: " + e.getMessage());
        }
    }

    private static void initializeDefaultPrices() {
        currentPrices.put("BTC", 45000.00);
        currentPrices.put("ETH", 3200.00);
        currentPrices.put("SOL", 102.00);
    }

    // PUBLIC STATIC METHODS
    public static void updateMarketPrices() {
        for (String symbol : currentPrices.keySet()) {
            double currentPrice = currentPrices.get(symbol);
            double newPrice = calculateNewPrice(symbol, currentPrice);
            currentPrices.put(symbol, newPrice);
        }
        saveMarketPrices();
    }

    private static double calculateNewPrice(String symbol, double currentPrice) {
        double changePercent;

        switch (symbol) {
            case "BTC":
                changePercent = (Math.random() * 8) - 3; // -3% to +5%
                break;
            case "ETH":
                changePercent = (Math.random() * 10) - 4; // -4% to +6%
                break;
            case "SOL":
                changePercent = (Math.random() * 14) - 6; // -6% to +8%
                break;
            default:
                changePercent = 0;
        }

        double newPrice = currentPrice * (1 + (changePercent / 100));
        return Math.round(newPrice * 100.0) / 100.0;
    }

    public static double getCurrentPrice(String symbol) {
        return currentPrices.getOrDefault(symbol.toUpperCase(), 0.0);
    }

    public static List<String> getAvailableSymbols() {
        return new ArrayList<>(currentPrices.keySet());
    }

    public static Map<String, Double> getAllPrices() {
        return new HashMap<>(currentPrices);
    }

    private static void saveMarketPrices() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MARKET_FILE))) {
            writer.write("asset_type,symbol,current_price");
            writer.newLine();

            // Map symbols back to their full names for saving
            for (Map.Entry<String, Double> entry : currentPrices.entrySet()) {
                String symbol = entry.getKey();
                double price = entry.getValue();
                String assetType = getAssetType(symbol);
                writer.write(assetType + "," + symbol + "," + price);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving market prices: " + e.getMessage());
        }
    }

    private static String getAssetType(String symbol) {
        switch (symbol) {
            case "BTC": return "bitcoin";
            case "ETH": return "ethereum";
            case "SOL": return "solana";
            default: return "unknown";
        }
    }
}