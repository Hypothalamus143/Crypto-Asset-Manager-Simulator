public class User {
    private String username;
    private double balance;
    // private List<Asset> assets; // Will implement later

    public User(String username) {
        this.username = username;
        this.balance = 0.0;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public double getNetProfit() {
        // Will implement with assets later
        return 0.0;
    }
}