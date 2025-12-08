import java.io.*;
import java.util.*;

public class UserRepository {
    private static UserRepository instance;
    private final String DATA_DIR = "data/users/";
    private final String AUTH_FILE = "data/auth.csv";

    // Private constructor to prevent instantiation
    private UserRepository() {
        new File(DATA_DIR).mkdirs();
        try {
            new File(AUTH_FILE).createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating auth file: " + e.getMessage());
        }
    }

    public void saveUserData(User user, String password) throws Exception{
        if(password == null)
            throw new Exception("Password is null");
        if(password.trim().isEmpty())
            throw new Exception("Password is Empty");
        saveUserToAuthFile(user.getUsername(), password);

        saveUserToFile(user);
    }

    // Private method for actual file writing
    private void saveUserToFile(User user) throws Exception{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getUserFilePath(user.getUsername())))) {
            // First line: balance,realized_profit
            writer.write(user.getBalance() + "," + user.getRealizedProfit());
            writer.newLine();

            // Save assets
            for (Asset asset : user.getAssets()) {
                writer.write(assetToCsvLine(asset));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    private boolean saveUserToAuthFile(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AUTH_FILE, true))) {
            writer.write(username + "," + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user to auth file: " + e.getMessage());
            return false;
        }
    }

    public User loadUser(String username) throws Exception {
        File userFile = new File(getUserFilePath(username));
        if (!userFile.exists()) {
            throw new Exception("No Users Found");
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
            throw new Exception("Error reading auth file: " + e.getMessage());
        }
        throw new Exception("User not Found");
    }

    private Asset parseAssetLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length >= 4) { // Changed from 5 to 4 (removed timestamp)
                String type = parts[0].trim();
                String symbol = parts[1].trim();
                double buyPrice = Double.parseDouble(parts[2].trim());
                double amount = Double.parseDouble(parts[3].trim());
                // Removed timestamp parsing
                return AssetFactory.getInstance().createAsset(symbol, buyPrice, amount);
            }
        } catch (Exception e) {
            System.err.println("Error parsing asset line: " + line);
        }
        return null;
    }

    private String assetToCsvLine(Asset asset) {
        String type = getAssetType(asset);
        // Removed timestamp from CSV line
        return String.format("%s,%s,%.2f,%.6f",
                type, asset.getSymbol(), asset.getBuyPrice(), asset.getAmount());
    }

    private String getAssetType(Asset asset) {
        return asset.getSymbol();
    }

    public boolean userExists(String username) {
        return userExistsInAuthFile(username);
    }

    private boolean userExistsInAuthFile(String username) {
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

    public boolean validateCredentials(String username, String password) throws Exception{
        try (BufferedReader reader = new BufferedReader(new FileReader(AUTH_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading auth file: " + e.getMessage());
        }
        throw new Exception("Invalid Username or Password");
    }
    public static UserRepository getInstance() {
        if(instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private String getUserFilePath(String username) {
        return DATA_DIR + username + ".csv";
    }
}