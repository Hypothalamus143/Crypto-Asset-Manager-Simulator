import java.io.*;
import java.util.*;

public class MarketManager {
    private final String MARKET_FILE = "data/market_prices.csv";
    private static MarketManager instance;
    private final Map<String, String> cryptoNames = new HashMap<>();
    private final Map<String, List<Double>> priceHistory = new HashMap<>();
    private final Map<String, Double> currentPrices = new HashMap<>();
    private final int MAX_PRICES = 100;

    private MarketManager(){
        cryptoNames.put("BTC", "Bitcoin");
        cryptoNames.put("ETH", "Ethereum");
        cryptoNames.put("SOL", "Solana");
        loadMarketPrices();
        for(int i = currentPrices.size(); i < 23; i++)
            updateMarketPrices();
    }

    private void loadMarketPrices() {
        File marketFile = new File(MARKET_FILE);

        if (!marketFile.exists()) {
            createDefaultMarketFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(marketFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Parse the line manually to handle JSON array in last column
                int firstComma = line.indexOf(',');
                int secondComma = line.indexOf(',', firstComma + 1);

                if (firstComma > 0 && secondComma > firstComma) {
                    String symbol = line.substring(0, firstComma).trim().toUpperCase();
                    String name = line.substring(firstComma + 1, secondComma).trim();
                    String pricesJson = line.substring(secondComma + 1).trim();

                    // Parse the JSON-like array: "[1.0,2.0,3.0]"
                    List<Double> prices = parsePriceArray(pricesJson);

                    if (!prices.isEmpty()) {
                        priceHistory.put(symbol, prices);
                        currentPrices.put(symbol, prices.get(prices.size() - 1));
                        cryptoNames.put(symbol, name);
                    }
                }
            }

            if (currentPrices.isEmpty()) {
                initializeDefaultPrices();
                saveMarketPrices();
            }

        } catch (IOException e) {
            System.err.println("Error loading market prices: " + e.getMessage());
            initializeDefaultPrices();
        }
    }

    private List<Double> parsePriceArray(String jsonArray) {
        List<Double> prices = new ArrayList<>();

        try {
            // Remove brackets and trim
            String content = jsonArray.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();

                if (!content.isEmpty()) {
                    // Split by comma and parse each value
                    String[] parts = content.split(",");
                    for (String part : parts) {
                        try {
                            prices.add(Double.parseDouble(part.trim()));
                        } catch (NumberFormatException e) {
                            // Skip invalid numbers
                            System.err.println("Warning: Invalid price value: " + part);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing price array: " + e.getMessage());
        }

        return prices;
    }

    private String formatPriceArray(List<Double> prices) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < prices.size(); i++) {
            sb.append(String.format("%.2f", prices.get(i)));
            if (i < prices.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void createDefaultMarketFile() {
        new File("data").mkdirs();
        initializeDefaultPrices();
        saveMarketPrices();
    }

    private void initializeDefaultPrices() {
        for (String symbol : cryptoNames.keySet()) {
            List<Double> prices = new ArrayList<>();
            double initialPrice = getDefaultPrice(symbol);
            prices.add(initialPrice);

            priceHistory.put(symbol, prices);
            currentPrices.put(symbol, initialPrice);
        }
    }

    private double getDefaultPrice(String symbol) {
        switch (symbol) {
            case "BTC": return 45000.00;
            case "ETH": return 3200.00;
            case "SOL": return 102.00;
            default: return 100.00;
        }
    }

    // PUBLIC STATIC METHODS
    public  void updateMarketPrices() {
        for (String symbol : currentPrices.keySet()) {
            double currentPrice = currentPrices.get(symbol);
            double newPrice = calculateNewPrice(symbol, currentPrice);

            currentPrices.put(symbol, newPrice);

            List<Double> prices = priceHistory.get(symbol);
            if (prices == null) {
                prices = new ArrayList<>();
                priceHistory.put(symbol, prices);
            }

            prices.add(newPrice);

            if (prices.size() > MAX_PRICES) {
                prices.remove(0);
            }
        }
        saveMarketPrices();
    }

    private  double calculateNewPrice(String symbol, double currentPrice) {
        double changePercent;

        switch (symbol) {
            case "BTC":
                changePercent = (Math.random() * 8) - 3;
                break;
            case "ETH":
                changePercent = (Math.random() * 10) - 4;
                break;
            case "SOL":
                changePercent = (Math.random() * 14) - 6;
                break;
            default:
                changePercent = 0;
        }

        double newPrice = currentPrice * (1 + (changePercent / 100));
        return Math.round(newPrice * 100.0) / 100.0;
    }

    private  void saveMarketPrices() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MARKET_FILE))) {
            writer.write("symbol,name,price_history");
            writer.newLine();

            for (Map.Entry<String, List<Double>> entry : priceHistory.entrySet()) {
                String symbol = entry.getKey();
                String name = cryptoNames.get(symbol);
                List<Double> prices = entry.getValue();

                writer.write(symbol + "," + name + "," + formatPriceArray(prices));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving market prices: " + e.getMessage());
        }
    }

    public double getCurrentPrice(String symbol) {
        return currentPrices.getOrDefault(symbol.toUpperCase(), 0.0);
    }

    public List<String> getAvailableSymbols() {
        return new ArrayList<>(currentPrices.keySet());
    }

    public Map<String, Double> getAllPrices() {
        return new HashMap<>(currentPrices);
    }

    public List<Double> getPriceHistory(String symbol) {
        return new ArrayList<>(priceHistory.getOrDefault(symbol, new ArrayList<>()));
    }

    public String getCryptoName(String symbol) {
        return cryptoNames.getOrDefault(symbol, "Unknown");
    }
    public static MarketManager getInstance(){
        if(instance == null)
            return new MarketManager();
        return instance;
    }
    public int getHistorySize(String symbol) {
        List<Double> history = priceHistory.get(symbol);
        return history != null ? history.size() : 0;
    }
}