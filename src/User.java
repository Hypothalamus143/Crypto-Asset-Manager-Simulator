public class User {
    private String username;
    private double balance;
    private double realizedProfit;
    // private List<Asset> assets; // Will be added later

    // Default constructor for new users
    public User(String username) {
        this.username = username;
        this.balance = 0.0;
        this.realizedProfit = 0.0;
        // this.assets = new ArrayList<>();
    }
    // Proper constructor for loading existing users
    public User(String username, double balance, double realizedProfit) { //, List<Asset> assets) {
        this.username = username;
        this.balance = balance;
        this.realizedProfit = realizedProfit;
        // this.assets = assets;
    }
    // Getters and setters
    public String getUsername() { return username; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public double getRealizedProfit() { return realizedProfit; }
    public void setRealizedProfit(double realizedProfit) { this.realizedProfit = realizedProfit; }

    public double getNetProfit() {
        // Total Net Profit = Realized Profit + Unrealized Profit from current assets
        double unrealizedProfit = 0.0;

        // TODO: Calculate unrealized profit from current assets
        // for (Asset asset : assets) {
        //     unrealizedProfit += asset.calculateUnrealizedProfit();
        // }

        return realizedProfit + unrealizedProfit;
    }
}