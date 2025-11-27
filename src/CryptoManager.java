import javax.swing.*;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
public class CryptoManager {
    private User currentUser;
    private Scanner scanner;

    public CryptoManager() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        // Launch GUI instead of terminal interface
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CryptoManagerGUI().show();
            }
        });
    }

    public void runPortfolioManager() {
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
                case "7":  // NEW: Sort Lots option
                    sortLots();
                    break;
                case "8":
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
        System.out.println("7. Sort Lots");  // Sort option
        System.out.println("8. Logout");
        System.out.printf("Current Sort: %s\n", Sorter.getCurrentSortDescription());
        System.out.print("Choose an option: ");
    }

    void sortLots() {
        System.out.println("\n--- Sort Lots ---");

        if (currentUser.getAssets().isEmpty()) {
            System.out.println("You don't have any assets to sort.");
            return;
        }

        // Get sort type from user
        System.out.println("Sort by:");
        System.out.println("1. Symbol");
        System.out.println("2. Total Value");
        System.out.println("3. Profit Amount");
        System.out.println("4. Profit Percentage");
        System.out.println("5. Buy Price");
        System.out.println("6. Amount");
        System.out.print("Choose (1-6, default 1): ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = 1;
        }

        // Get sort direction from user
        System.out.println("Direction:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Choose (1-2, default 2): ");

        boolean ascending;
        try {
            int dirChoice = Integer.parseInt(scanner.nextLine().trim());
            ascending = (dirChoice == 1);
        } catch (NumberFormatException e) {
            ascending = false;
        }

        // Create the appropriate comparator
        Comparator<Asset> comparator;
        switch (choice) {
            case 1:
                comparator = new Asset.SymbolComparator(ascending);
                break;
            case 2:
                comparator = new Asset.TotalValueComparator(ascending);
                break;
            case 3:
                comparator = new Asset.ProfitAmountComparator(ascending);
                break;
            case 4:
                comparator = new Asset.ProfitPercentComparator(ascending);
                break;
            case 5:
                comparator = new Asset.BuyPriceComparator(ascending);
                break;
            case 6:
                comparator = new Asset.AmountComparator(ascending);
                break;
            default:
                comparator = new Asset.SymbolComparator(ascending);
        }

        // Set the sorter AND actually sort the assets
        Sorter.setSorter(comparator);
        Sorter.sort(currentUser.getAssets());

        System.out.printf("\nSort order changed to: %s\n", Sorter.getCurrentSortDescription());
        System.out.println("Portfolio has been sorted.");

        // Use the existing viewPortfolio function to show the result
        viewPortfolio();
    }

    private void viewPortfolio() {
        System.out.println("\n--- Your Portfolio ---");
        System.out.printf("Username: %s\n", currentUser.getUsername());
        System.out.printf("Balance: $%,.2f\n", currentUser.getBalance());
        System.out.printf("Realized Profit: $%,.2f\n", currentUser.getRealizedProfit());
        System.out.printf("Net Profit: $%,.2f\n", currentUser.getNetProfit());
        System.out.printf("Sort Order: %s\n", Sorter.getCurrentSortDescription());

        System.out.println("\nYour Lots:");
        System.out.println("==========");

        List<Asset> assets = currentUser.getAssets();

        if (assets.isEmpty()) {
            System.out.println("No assets yet. Use 'Buy Crypto' to get started!");
        } else {
            for (int i = 0; i < assets.size(); i++) {
                Asset asset = assets.get(i);
                double currentPrice = asset.getCurrentPrice();
                double unrealizedProfit = asset.getUnrealizedProfit();
                double profitPercentage = (currentPrice - asset.getBuyPrice()) / asset.getBuyPrice() * 100;

                System.out.printf("[%d] %.6f %s | Buy: $%,.2f | Current: $%,.2f\n",
                        i + 1, asset.getAmount(), asset.getSymbol(),
                        asset.getBuyPrice(), currentPrice);
                System.out.printf("     Value: $%,.2f | P/L: $%,.2f (%.2f%%)\n",
                        asset.getTotalValue(), unrealizedProfit, profitPercentage);
                System.out.println();
            }

            double totalPortfolioValue = currentUser.getBalance();
            for (Asset asset : assets) {
                totalPortfolioValue += asset.getTotalValue();
            }
            System.out.printf("Total Portfolio Value: $%,.2f\n", totalPortfolioValue);
        }
    }

    void buyCrypto() {
        System.out.println("\n--- Buy Crypto ---");

        // Show available cryptocurrencies
        System.out.println("Available Cryptocurrencies:");
        Map<String, Double> marketPrices = MarketManager.getAllPrices();
        int index = 1;
        List<String> symbols = new ArrayList<>(marketPrices.keySet());

        for (String symbol : symbols) {
            String assetName = getAssetName(symbol);
            double price = marketPrices.get(symbol);
            System.out.printf("%d. %s (%s) - $%,.2f\n", index++, assetName, symbol, price);
        }

        // Get user selection
        System.out.print("Select cryptocurrency (1-" + symbols.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > symbols.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            String selectedSymbol = symbols.get(choice - 1);
            String assetName = getAssetName(selectedSymbol);
            double currentPrice = marketPrices.get(selectedSymbol);

            // Get amount to buy
            System.out.printf("Current %s price: $%,.2f\n", assetName, currentPrice);
            System.out.print("Enter amount to buy: ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            // Calculate total cost
            double totalCost = currentPrice * amount;

            // Check if user has enough balance
            if (totalCost > currentUser.getBalance()) {
                System.out.printf("Insufficient funds. You need $%,.2f but only have $%,.2f\n",
                        totalCost, currentUser.getBalance());
                return;
            }

            // Confirm purchase
            System.out.printf("\nPurchase Summary:\n");
            System.out.printf("Asset: %s (%s)\n", assetName, selectedSymbol);
            System.out.printf("Amount: %.6f\n", amount);
            System.out.printf("Price: $%,.2f\n", currentPrice);
            System.out.printf("Total Cost: $%,.2f\n", totalCost);
            System.out.print("Confirm purchase? (yes/no): ");

            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (!confirmation.equals("yes") && !confirmation.equals("y")) {
                System.out.println("Purchase cancelled.");
                return;
            }

            // Execute purchase
            executePurchase(selectedSymbol, currentPrice, amount, totalCost);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    private void executePurchase(String symbol, double buyPrice, double amount, double totalCost) {
        // Create the asset
        Asset newAsset = createAsset(symbol, buyPrice, amount);

        if (newAsset != null) {
            // Update user's balance and assets
            currentUser.setBalance(currentUser.getBalance() - totalCost);
            currentUser.addAsset(newAsset);

            // AUTO-SORT after buying
            Sorter.sort(currentUser.getAssets());

            System.out.printf("\nPurchase successful!\n");
            System.out.printf("Bought %.6f %s at $%,.2f each\n", amount, symbol, buyPrice);
            System.out.printf("Total cost: $%,.2f\n", totalCost);
            System.out.printf("New balance: $%,.2f\n", currentUser.getBalance());
            UserRepository.saveUserData(currentUser, null);
        } else {
            System.out.println("Error: Could not create asset.");
        }
    }

    private Asset createAsset(String symbol, double buyPrice, double amount) {
        switch (symbol.toUpperCase()) {
            case "BTC":
                return new Bitcoin(buyPrice, amount);
            case "ETH":
                return new Ethereum(buyPrice, amount);
            case "SOL":
                return new Solana(buyPrice, amount);
            default:
                return null;
        }
    }

    String getAssetName(String symbol) {
        switch (symbol.toUpperCase()) {
            case "BTC": return "Bitcoin";
            case "ETH": return "Ethereum";
            case "SOL": return "Solana";
            default: return "Unknown";
        }
    }

    void sellCrypto() {
        System.out.println("\n--- Sell Crypto ---");

        List<Asset> assets = currentUser.getAssets();

        if (assets.isEmpty()) {
            System.out.println("You don't own any assets to sell.");
            return;
        }

        viewPortfolio();

        try {
            // Select lot to sell from
            System.out.print("\nSelect lot to sell from (1-" + assets.size() + "): ");
            int lotChoice = Integer.parseInt(scanner.nextLine().trim());
            if (lotChoice < 1 || lotChoice > assets.size()) {
                System.out.println("Invalid lot selection.");
                return;
            }

            Asset selectedAsset = assets.get(lotChoice - 1);
            double currentPrice = selectedAsset.getCurrentPrice();

            // Get amount to sell
            System.out.printf("Enter amount to sell (max %.6f): ", selectedAsset.getAmount());
            double amountToSell = Double.parseDouble(scanner.nextLine().trim());

            if (amountToSell <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }

            if (amountToSell > selectedAsset.getAmount()) {
                System.out.printf("Insufficient amount. You only have %.6f in this lot.\n", selectedAsset.getAmount());
                return;
            }

            // Confirm sale
            double totalValue = amountToSell * currentPrice;
            double realizedProfit = (currentPrice - selectedAsset.getBuyPrice()) * amountToSell;

            System.out.printf("\nSale Summary:\n");
            System.out.printf("Asset: %s (%s)\n", selectedAsset.getName(), selectedAsset.getSymbol());
            System.out.printf("Amount: %.6f\n", amountToSell);
            System.out.printf("Sell Price: $%,.2f\n", currentPrice);
            System.out.printf("Total Value: $%,.2f\n", totalValue);
            System.out.printf("Realized Profit: $%,.2f\n", realizedProfit);
            System.out.print("Confirm sale? (yes/no): ");

            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (!confirmation.equals("yes") && !confirmation.equals("y")) {
                System.out.println("Sale cancelled.");
                return;
            }

            // Execute sale (same as before)
            executeSale(selectedAsset, amountToSell, currentPrice, realizedProfit, totalValue);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    private Map<String, List<Asset>> groupAssetsBySymbol() {
        Map<String, List<Asset>> assetsBySymbol = new HashMap<>();
        for (Asset asset : currentUser.getAssets()) {
            assetsBySymbol.computeIfAbsent(asset.getSymbol(), k -> new ArrayList<>()).add(asset);
        }
        return assetsBySymbol;
    }

    private void executeSale(Asset asset, double amountToSell, double sellPrice, double realizedProfit, double totalValue) {
        // Update user's balance
        currentUser.setBalance(currentUser.getBalance() + totalValue);

        // Update realized profit
        currentUser.setRealizedProfit(currentUser.getRealizedProfit() + realizedProfit);

        // Update asset amount or remove if fully sold
        if (amountToSell == asset.getAmount()) {
            // Fully sold - remove the asset
            currentUser.getAssets().remove(asset);
        } else {
            // Partially sold - reduce amount
            asset.setAmount(asset.getAmount() - amountToSell);
        }
        // AUTO-SORT after selling (in case removal changed order)
        Sorter.sort(currentUser.getAssets());

        System.out.printf("Sale completed!\n");
        System.out.printf("Received: $%,.2f\n", totalValue);
        System.out.printf("Realized Profit: $%,.2f\n", realizedProfit);
        System.out.printf("New balance: $%,.2f\n", currentUser.getBalance());
        UserRepository.saveUserData(currentUser, null);
    }

    void checkMarket() {
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

    void deposit() {
        double amount = CryptoManagerGUI.showDepositGUI(currentUser.getBalance());

        if (amount > 0) {
            // Execute the deposit
            double oldBalance = currentUser.getBalance();
            double newBalance = oldBalance + amount;
            currentUser.setBalance(newBalance);

            System.out.printf("Successfully deposited $%.2f\n", amount);
            System.out.printf("Old balance: $%.2f\n", oldBalance);
            System.out.printf("New balance: $%.2f\n", newBalance);
            UserRepository.saveUserData(currentUser, null);

            JOptionPane.showMessageDialog(null,
                    String.format("Deposited $%,.2f successfully!\nNew balance: $%,.2f", amount, newBalance),
                    "Deposit Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void withdraw() {
        double amount = CryptoManagerGUI.showWithdrawGUI(currentUser.getBalance());

        if (amount > 0) {
            // Execute the withdrawal
            double oldBalance = currentUser.getBalance();
            double newBalance = oldBalance - amount;
            currentUser.setBalance(newBalance);

            System.out.printf("Successfully withdrew $%.2f\n", amount);
            System.out.printf("Old balance: $%.2f\n", oldBalance);
            System.out.printf("New balance: $%.2f\n", newBalance);
            UserRepository.saveUserData(currentUser, null);

            JOptionPane.showMessageDialog(null,
                    String.format("Withdrew $%,.2f successfully!\nNew balance: $%,.2f", amount, newBalance),
                    "Withdrawal Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}