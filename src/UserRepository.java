import java.io.*;
import java.util.*;

public class UserRepository {
    private static final String DATA_DIR = "data/users/";
    private static final String AUTH_FILE = "data/auth.csv";

    // Static initializer to set up directories
    static {
        new File(DATA_DIR).mkdirs();
        try {
            new File(AUTH_FILE).createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating auth file: " + e.getMessage());
        }
    }

    // Private constructor to prevent instantiation
    private UserRepository() {}

    public static boolean saveUserData(User user, String password) {
        // If password is provided, also save to auth file (for new users)
        if (password != null && !password.isEmpty()) {
            if (!saveUserToAuthFile(user.getUsername(), password)) {
                return false;
            }
        }

        // Always save user data to their file
        return saveUserToFile(user);
    }

    // Private method for actual file writing
    private static boolean saveUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getUserFilePath(user.getUsername())))) {
            // First line: balance,realized_profit
            writer.write(user.getBalance() + "," + user.getRealizedProfit());
            writer.newLine();

            // Save assets
            for (Asset asset : user.getAssets()) {
                writer.write(assetToCsvLine(asset));
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            return false;
        }
    }

    private static boolean saveUserToAuthFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AUTH_FILE, true))) {
            writer.write(username + "," + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user to auth file: " + e.getMessage());
            return false;
        }
    }

    public static User loadUser(String username) {
        File userFile = new File(getUserFilePath(username));
        if (!userFile.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            // First line: balance,realized_profit
            String firstLine = reader.readLine();
            if (firstLine != null) {
                String[] parts = firstLine.split(",");
                if (parts.length >= 2) {
                    double balance = Double.parseDouble(parts[0].trim());
                    double realizedProfit = Double.parseDouble(parts[1].trim());
                    List<Asset> assets = new ArrayList<>();

                    // Load assets from subsequent lines
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Asset asset = parseAssetLine(line);
                        if (asset != null) {
                            assets.add(asset);
                        }
                    }

                    return new User(username, balance, realizedProfit, assets);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading user: " + e.getMessage());
        }
        return null;
    }

    private static Asset parseAssetLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length >= 4) { // Changed from 5 to 4 (removed timestamp)
                String type = parts[0].trim();
                String symbol = parts[1].trim();
                double buyPrice = Double.parseDouble(parts[2].trim());
                double amount = Double.parseDouble(parts[3].trim());
                // Removed timestamp parsing

                switch (type.toLowerCase()) {
                    case "bitcoin":
                        return new Bitcoin(buyPrice, amount);
                    case "ethereum":
                        return new Ethereum(buyPrice, amount);
                    case "solana":
                        return new Solana(buyPrice, amount);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing asset line: " + line);
        }
        return null;
    }

    private static String assetToCsvLine(Asset asset) {
        String type = getAssetType(asset);
        // Removed timestamp from CSV line
        return String.format("%s,%s,%.2f,%.6f",
                type, asset.getSymbol(), asset.getBuyPrice(), asset.getAmount());
    }

    private static String getAssetType(Asset asset) {
        if (asset instanceof Bitcoin) return "bitcoin";
        if (asset instanceof Ethereum) return "ethereum";
        if (asset instanceof Solana) return "solana";
        return "unknown";
    }

    public static boolean userExists(String username) {
        return userExistsInAuthFile(username);
    }

    private static boolean userExistsInAuthFile(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(AUTH_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading auth file: " + e.getMessage());
        }
        return false;
    }

    public static boolean validateCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(AUTH_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading auth file: " + e.getMessage());
        }
        return false;
    }

    private static String getUserFilePath(String username) {
        return DATA_DIR + username + ".csv";
    }
}