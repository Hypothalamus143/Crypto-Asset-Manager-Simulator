import java.util.Map;
import java.util.Scanner;
import java.util.List;
public class CryptoManager {
    private User currentUser;
    private Scanner scanner;

    public CryptoManager() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            // Handle authentication first
            this.currentUser = AuthManager.authenticateUser();

            if (currentUser != null) {
                runPortfolioManager();
            } else {
                // If authenticateUser returns null, it means user chose to exit
                running = false;
            }
        }

        System.out.println("Thank you for using Crypto Portfolio Manager!");
    }

    private void runPortfolioManager() {
        System.out.println("\nWelcome to your Crypto Portfolio, " + currentUser.getUsername() + "!");

        boolean inPortfolio = true;
        while (inPortfolio) {
            showMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewPortfolio();
                    break;
                case "2":
                    buyCrypto();
                    break;
                case "3":
                    sellCrypto();
                    break;
                case "4":
                    checkMarket();
                    break;
                case "5":
                    deposit();
                    break;
                case "6":
                    withdraw();
                    break;
                case "7":
                    inPortfolio = false;
                    AuthManager.logout();
                    System.out.println("Returning to login screen...\n");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View Portfolio");
        System.out.println("2. Buy Crypto");
        System.out.println("3. Sell Crypto");
        System.out.println("4. Check Market Prices");
        System.out.println("5. Deposit Funds");
        System.out.println("6. Withdraw Funds");
        System.out.println("7. Logout");
        System.out.print("Choose an option: ");
    }

    private void viewPortfolio() {
        System.out.println("\n--- Your Portfolio ---");
        System.out.printf("Username: %s\n", currentUser.getUsername());
        System.out.printf("Balance: $%.2f\n", currentUser.getBalance());
        System.out.printf("Net Profit/Loss: $%.2f\n", currentUser.getNetProfit());

        System.out.println("\nYour Assets:");
        System.out.println("(Asset display will be implemented when Asset class is ready)");
    }

    private void buyCrypto() {
        System.out.println("\n--- Buy Crypto ---");
        System.out.println("(Buy functionality will be implemented when AssetFactory is ready)");
        // Implementation pending...
    }

    private void sellCrypto() {
        System.out.println("\n--- Sell Crypto ---");
        System.out.println("(Sell functionality will be implemented when Asset management is ready)");
        // Implementation pending...
    }

    private void checkMarket() {
        System.out.println("\n--- Market Prices ---");

        // Update all asset prices using static method
        MarketManager.updateMarketPrices();

        // Display current prices
        Map<String, Double> marketPrices = MarketManager.getAllPrices();
        System.out.println("Current Market Prices:");
        System.out.println("======================");

        for (Map.Entry<String, Double> entry : marketPrices.entrySet()) {
            String symbol = entry.getKey();
            double price = entry.getValue();
            String assetName = getAssetName(symbol);
            System.out.printf("- %s (%s): $%,.2f\n", assetName, symbol, price);
        }

        System.out.println("\nMarket prices have been updated!");
        System.out.println("These new prices will be used for any new purchases.");
    }

    private String getAssetName(String symbol) {
        switch (symbol) {
            case "BTC": return "Bitcoin";
            case "ETH": return "Ethereum";
            case "SOL": return "Solana";
            default: return "Unknown";
        }
    }

    private void deposit() {
        System.out.println("\n--- Deposit Funds ---");
        System.out.print("Enter amount to deposit: $");
        String amountInput = scanner.nextLine();

        try {
            double amount = Double.parseDouble(amountInput);

            if (amount <= 0) {
                System.out.println("Error: Deposit amount must be positive.");
                return;
            }

            if (amount > 1000000) { // Reasonable limit
                System.out.println("Error: Deposit amount cannot exceed $1,000,000.");
                return;
            }

            double oldBalance = currentUser.getBalance();
            double newBalance = oldBalance + amount;
            currentUser.setBalance(newBalance);

            System.out.printf("Successfully deposited $%.2f\n", amount);
            System.out.printf("Old balance: $%.2f\n", oldBalance);
            System.out.printf("New balance: $%.2f\n", newBalance);
            System.out.println("Note: Balance will be saved when you logout.");

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount. Please enter a valid number.");
        }
    }

    private void withdraw() {
        System.out.println("\n--- Withdraw Funds ---");
        System.out.print("Enter amount to withdraw: $");
        String amountInput = scanner.nextLine();

        try {
            double amount = Double.parseDouble(amountInput);

            if (amount <= 0) {
                System.out.println("Error: Withdrawal amount must be positive.");
                return;
            }

            double currentBalance = currentUser.getBalance();

            if (amount > currentBalance) {
                System.out.printf("Error: Insufficient funds. You tried to withdraw $%.2f but only have $%.2f available.\n",
                        amount, currentBalance);
                return;
            }

            double oldBalance = currentBalance;
            double newBalance = oldBalance - amount;
            currentUser.setBalance(newBalance);

            System.out.printf("Successfully withdrew $%.2f\n", amount);
            System.out.printf("Old balance: $%.2f\n", oldBalance);
            System.out.printf("New balance: $%.2f\n", newBalance);
            System.out.println("Note: Balance will be saved when you logout.");

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount. Please enter a valid number.");
        }
    }
}