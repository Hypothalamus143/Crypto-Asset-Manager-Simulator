import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AssetRegistry {
    private static AssetRegistry instance;
    private final List<AssetMetadata> registeredMetadata = new ArrayList<>();
    private final String DATA_DIR = "data";
    private final String CSV_FILE = DATA_DIR + File.separator + "assetmetadata.csv";

    private AssetRegistry() {
        new File(DATA_DIR).mkdirs();
        loadFromCSV();
    }

    // ========== CSV SAVE/LOAD ==========

    public  void saveToCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.write("symbol,name,description,category,priceChangeRange,priceChangeOffset,defaultPrice,currentPrice,priceHistory");
            writer.newLine();

            // Write each metadata
            for (AssetMetadata metadata : registeredMetadata) {
                // Convert price history list to semicolon-separated string in brackets
                String priceHistoryStr = convertPriceHistoryToString(metadata.getPriceHistory());

                String line = String.format("%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%s",
                        metadata.getSymbol(),
                        escapeCommas(metadata.getName()),
                        escapeCommas(metadata.getDescription()),
                        escapeCommas(metadata.getCategory()),
                        metadata.getPriceChangeRange(),
                        metadata.getPriceChangeOffset(),
                        metadata.getDefaultPrice(),
                        metadata.getCurrentPrice(),
                        priceHistoryStr
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving to CSV: " + e.getMessage());
        }
    }

    public  void loadFromCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            System.out.println("CSV file not found, starting with empty registry");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }

                String[] parts = line.split(",", -1); // -1 to keep trailing empty strings
                if (parts.length < 9) {
                    System.err.println("Invalid CSV line, skipping: " + line);
                    continue;
                }

                // Parse fields
                String symbol = parts[0];
                String name = unescapeCommas(parts[1]);
                String description = unescapeCommas(parts[2]);
                String category = unescapeCommas(parts[3]);
                double priceChangeRange = Double.parseDouble(parts[4]);
                double priceChangeOffset = Double.parseDouble(parts[5]);
                double defaultPrice = Double.parseDouble(parts[6]);
                double currentPrice = Double.parseDouble(parts[7]);
                List<Double> priceHistory = parsePriceHistoryString(parts[8]);

                // Create metadata
                AssetMetadata metadata = new AssetMetadata(
                        symbol, name, description, category,
                        priceChangeRange, priceChangeOffset,
                        defaultPrice, currentPrice
                );

                // Set price history
                metadata.setPriceHistory(priceHistory);

                // Register
                registeredMetadata.add(metadata);
            }

            System.out.println("Loaded " + registeredMetadata.size() + " assets from CSV");

        } catch (IOException e) {
            System.err.println("Error loading from CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing number in CSV: " + e.getMessage());
        }
    }

    // ========== HELPER METHODS ==========

    private  String convertPriceHistoryToString(List<Double> priceHistory) {
        if (priceHistory == null || priceHistory.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < priceHistory.size(); i++) {
            if (i > 0) sb.append(";");
            sb.append(String.format("%.2f", priceHistory.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private  List<Double> parsePriceHistoryString(String priceHistoryStr) {
        List<Double> priceHistory = new ArrayList<>();

        if (priceHistoryStr == null || priceHistoryStr.trim().isEmpty() ||
                priceHistoryStr.equals("[]")) {
            return priceHistory;
        }

        // Remove brackets and split by semicolon
        String cleanStr = priceHistoryStr.trim();
        if (cleanStr.startsWith("[") && cleanStr.endsWith("]")) {
            cleanStr = cleanStr.substring(1, cleanStr.length() - 1);
        }

        if (!cleanStr.isEmpty()) {
            String[] priceStrs = cleanStr.split(";");
            for (String priceStr : priceStrs) {
                try {
                    priceHistory.add(Double.parseDouble(priceStr.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing price in history: " + priceStr);
                }
            }
        }

        return priceHistory;
    }

    private  String escapeCommas(String str) {
        if (str == null) return "";
        return str.replace(",", ";");
    }

    private  String unescapeCommas(String str) {
        if (str == null) return "";
        return str.replace(";", ",");
    }

    // ========== REGISTRY METHODS ==========

    public  void register(AssetMetadata metadata) {
        if (metadata == null) {
            throw new IllegalArgumentException("Metadata cannot be null");
        }

        // Check if symbol already exists
        String symbol = metadata.getSymbol();
        if (get(symbol) != null) {
            System.out.println("Symbol already registered: " + symbol);
            //throw new IllegalArgumentException("Symbol already registered: " + symbol);
        }

        registeredMetadata.add(metadata);
        saveToCSV(); // Auto-save after registration
    }

    public  AssetMetadata get(String symbol) {
        if (symbol == null) return null;

        String searchSymbol = symbol.trim();
        for (AssetMetadata metadata : registeredMetadata) {
            if (metadata.getSymbol().equalsIgnoreCase(searchSymbol)) {
                return metadata;
            }
        }
        return null;
    }

    public  boolean contains(String symbol) {
        return get(symbol) != null;
    }

    public  void remove(String symbol) {
        if (symbol == null) return;

        String searchSymbol = symbol.trim();
        for (int i = 0; i < registeredMetadata.size(); i++) {
            AssetMetadata metadata = registeredMetadata.get(i);
            if (metadata.getSymbol().equalsIgnoreCase(searchSymbol)) {
                registeredMetadata.remove(i);
                saveToCSV(); // Auto-save after removal
                return;
            }
        }
    }

    public  void clear() {
        registeredMetadata.clear();
        saveToCSV(); // Auto-save after clear
    }

    public  List<AssetMetadata> getAll() {
        List<AssetMetadata> copy = new ArrayList<>();
        for (AssetMetadata metadata : registeredMetadata) {
            copy.add(metadata);
        }
        return copy;
    }

    public  List<String> getAllSymbols() {
        List<String> symbols = new ArrayList<>();
        for (AssetMetadata metadata : registeredMetadata) {
            symbols.add(metadata.getSymbol());
        }
        return symbols;
    }

    public  int size() {
        return registeredMetadata.size();
    }

    public  boolean isEmpty() {
        return registeredMetadata.isEmpty();
    }

    // Manual save if needed (though auto-save is already done)
    public  void save() {
        saveToCSV();
    }
    public  void updateAllAssetPrices() {
        for (AssetMetadata metadata : registeredMetadata) {
            metadata.updatePriceWithRandomChange();
        }
        saveToCSV(); // Auto-save after updating all prices
    }
    public  List<Double> getPriceHistory(String symbol) {
        AssetMetadata metadata = get(symbol);
        if (metadata != null) {
            return metadata.getPriceHistory();
        }
        return new ArrayList<>(); // Return empty list if symbol not found
    }
    public  String getName(String symbol){
        AssetMetadata metadata = get(symbol);
        if (metadata != null) {
            return metadata.getName();
        }
        return "Unknown";
    }
    public  Map<String, Double> getAllCurrentPrices() {
        Map<String, Double> priceMap = new HashMap<>();
        for (AssetMetadata metadata : registeredMetadata) {
            priceMap.put(metadata.getSymbol(), metadata.getCurrentPrice());
        }
        return priceMap;
    }
    public  double getCurrentPrice(String symbol){
        AssetMetadata metadata = get(symbol);
        if (metadata != null) {
            return metadata.getCurrentPrice();
        }
        return 0;
    }
    // Manual reload if needed
    public  void reload() {
        registeredMetadata.clear();
        loadFromCSV();
    }
    public static AssetRegistry getInstance(){
        if(instance == null)
            return new AssetRegistry();
        return instance;
    }
}