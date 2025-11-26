import java.util.*;

public class User {
    private String username;
    private double balance;
    private double realizedProfit;
    private List<Asset> assets; // Add assets list

    // Default constructor for new users
    public User(String username) {
        this.username = username;
        this.balance = 0.0;
        this.realizedProfit = 0.0;
        this.assets = new ArrayList<>();
    }

    // Proper constructor for loading existing users
    public User(String username, double balance, double realizedProfit, List<Asset> assets) {
        this.username = username;
        this.balance = balance;
        this.realizedProfit = realizedProfit;
        this.assets = assets != null ? assets : new ArrayList<>();
    }

    // Getters and setters
    public String getUsername() { return username; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getRealizedProfit() { return realizedProfit; }
    public void setRealizedProfit(double realizedProfit) { this.realizedProfit = realizedProfit; }
    public List<Asset> getAssets() { return assets; }

    public double getNetProfit() {
        double unrealizedProfit = 0.0;
        for (Asset asset : assets) {
            unrealizedProfit += asset.getUnrealizedProfit();
        }
        return realizedProfit + unrealizedProfit;
    }

    public void addAsset(Asset asset) {
        this.assets.add(asset);
    }

    public boolean removeAsset(Asset asset) {
        return this.assets.remove(asset);
    }
}