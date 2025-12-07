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
    private static CryptoManager instance;
    private AuthManager authManager;
    private MarketManager marketManager;
    private UserRepository userRepository;
    private AssetFactory assetFactory;
    private CryptoManagerGUI cryptoManagerGUI;
    private CryptoManager() {
        this.scanner = new Scanner(System.in);
        authManager = AuthManager.getInstance();
        assetFactory = AssetFactory.getInstance();
        userRepository = UserRepository.getInstance();
        marketManager = MarketManager.getInstance();
        cryptoManagerGUI = CryptoManagerGUI.getInstance(marketManager);
    }

    public void start() {
        int choice;

        do {
            choice = cryptoManagerGUI.getLandingChoice();

            switch(choice) {
                case 1: // Login
                    handleLogin();
                    break;
                case 2: // Create Account
                    handleCreateAccount();
                    break;
                case 3: // Exit
                    handleExit();
                    break;
            }
        } while (choice != 3);
    }

    private void runPortfolioManager() {
        System.out.println("\n=== Portfolio Manager Started ===");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        cryptoManagerGUI.setCurrentUser(currentUser);
        boolean inPortfolio = true;

        while (inPortfolio) {
            cryptoManagerGUI.showPortfolioPanel();
            // Get portfolio choice from GUI (this blocks)
            int choice = cryptoManagerGUI.getPortfolioChoice();

            System.out.println("DEBUG: Received portfolio choice: " + choice);

            switch (choice) {
                case CryptoManagerGUI.PORTFOLIO_BUY: // 2
                    buyCrypto();
                    break;
                case CryptoManagerGUI.PORTFOLIO_SELL: // 3
                    sellCrypto();
                    break;
                case CryptoManagerGUI.PORTFOLIO_REFRESH_PRICES: // 4
                    checkMarket();
                    break;

                case CryptoManagerGUI.PORTFOLIO_DEPOSIT: // 5
                    deposit();
                    break;

                case CryptoManagerGUI.PORTFOLIO_WITHDRAW: // 6
                    withdraw();
                    break;
                case CryptoManagerGUI.PORTFOLIO_SORT: // 7
                    sortLots();
                    break;
                case CryptoManagerGUI.PORTFOLIO_LOGOUT: // 8
                    inPortfolio = false;
                    break;

                default:
                    System.out.println("DEBUG: Unknown portfolio choice: " + choice);
            }
        }

        System.out.println("=== Returning to main menu ===");
    }

    private void handleExit() {
        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to exit Crypto Portfolio Manager?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Exiting Crypto Portfolio Manager...");

            // Close GUI gracefully
            cryptoManagerGUI.close();

            // Exit application
            System.exit(0);
        } else {
            System.out.println("Exit cancelled by user");
            // Loop continues, showing landing choice again
        }
    }

    private void handleLogin() {
        boolean loggedIn = false;
        LoginAttempt lastAttempt = null;

        while (!loggedIn) {
            // Show login GUI with previous attempt values
            LoginAttempt loginAttempt = cryptoManagerGUI.showLoginGUI(lastAttempt);

            if (loginAttempt == null) {
                // User cancelled
                System.out.println("Login cancelled");
                break;
            }

            // Store attempt for potential retry (with all values preserved)
            lastAttempt = loginAttempt;

            User user = authManager.login(loginAttempt);

            if (user != null) {
                currentUser = user;
                loggedIn = true;
                runPortfolioManager();
            }
            // If login fails, loop continues with all values preserved
        }
    }

    private void handleCreateAccount() {
        boolean accountCreated = false;
        CreateAccountRequest lastRequest = null;

        while (!accountCreated) {
            // Show create account GUI with previous request values
            CreateAccountRequest request = cryptoManagerGUI.showCreateAccountGUI(lastRequest);

            if (request == null) {
                // User cancelled
                System.out.println("Account creation cancelled");
                break;
            }

            // Store request for potential retry (with all values preserved)
            lastRequest = request;

            accountCreated = authManager.createAccount(request);

            if (accountCreated) {
                System.out.println("Account created successfully!");
            }
            // If creation fails, loop continues with all values preserved
        }
    }

    public void sortLots() {
        System.out.println("\n--- Sort Lots ---");
        String sortBy = cryptoManagerGUI.getCurrentSortBy();
        String direction = cryptoManagerGUI.getCurrentSortDirection();
        if (currentUser.getAssets().isEmpty()) {
            System.out.println("You don't have any assets to sort.");
            return;
        }

        boolean ascending = direction.equals("Ascending");

        // Create the appropriate comparator
        Comparator<Asset> comparator;
        switch (sortBy) {
            case "Symbol":
                comparator = new Asset.SymbolComparator(ascending);
                break;
            case "Total Value":
                comparator = new Asset.TotalValueComparator(ascending);
                break;
            case "Profit Amount":
                comparator = new Asset.ProfitAmountComparator(ascending);
                break;
            case "Profit Percentage":
                comparator = new Asset.ProfitPercentComparator(ascending);
                break;
            case "Buy Price":
                comparator = new Asset.BuyPriceComparator(ascending);
                break;
            case "Amount":
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

    public void buyCrypto() {
        String symbol = cryptoManagerGUI.getBuyChoice();
        double currentPrice = marketManager.getCurrentPrice(symbol);
        double amount = cryptoManagerGUI.showBuyCryptoGUI(symbol, currentPrice, currentUser.getBalance());
        if(amount <= 0)
            return;
        System.out.println("\n--- Buy Crypto ---");

        double totalCost = currentPrice * amount;

        // Check if user has enough balance
        if (totalCost > currentUser.getBalance()) {
            System.out.printf("Insufficient funds. You need $%,.2f but only have $%,.2f\n",
                    totalCost, currentUser.getBalance());
            return;
        }

        // Confirm purchase (in terminal, we'll assume yes since GUI already confirmed)
        System.out.printf("\nPurchase Summary:\n");
        System.out.printf("Asset: %s (%s)\n", getAssetName(symbol), symbol);
        System.out.printf("Amount: %.6f\n", amount);
        System.out.printf("Price: $%,.2f\n", currentPrice);
        System.out.printf("Total Cost: $%,.2f\n", totalCost);
        System.out.println("Purchase confirmed via GUI.");

        // Execute purchase
        executePurchase(symbol, currentPrice, amount, totalCost);
    }


    private void executePurchase(String symbol, double buyPrice, double amount, double totalCost) {
        // Create the asset
        Asset newAsset = assetFactory.createAsset(symbol, buyPrice, amount);

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
            userRepository.saveUserData(currentUser, null);
        } else {
            System.out.println("Error: Could not create asset.");
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

    public void sellCrypto() {
        System.out.println("\n--- Sell Crypto ---");
        Asset asset = cryptoManagerGUI.getCurrentAsset();
        double amountToSell = cryptoManagerGUI.showSellCryptoGUI(asset);
        double currentPrice = asset.getCurrentPrice();
        double totalValue = amountToSell * currentPrice;
        double realizedProfit = (currentPrice - asset.getBuyPrice()) * amountToSell;

        System.out.printf("\nSale Summary:\n");
        System.out.printf("Asset: %s (%s)\n", asset.getName(), asset.getSymbol());
        System.out.printf("Amount: %.6f\n", amountToSell);
        System.out.printf("Sell Price: $%,.2f\n", currentPrice);
        System.out.printf("Total Value: $%,.2f\n", totalValue);
        System.out.printf("Realized Profit: $%,.2f\n", realizedProfit);
        System.out.println("Sale confirmed via GUI.");

        // Execute sale
        executeSale(asset, amountToSell, currentPrice, realizedProfit, totalValue);
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
        userRepository.saveUserData(currentUser, null);
    }

    void checkMarket() {
        System.out.println("\n--- Market Prices ---");

        // Update all asset prices using static method
        marketManager.updateMarketPrices();

        // Display current prices
        Map<String, Double> marketPrices = marketManager.getAllPrices();
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
            userRepository.saveUserData(currentUser, null);

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
            userRepository.saveUserData(currentUser, null);

            JOptionPane.showMessageDialog(null,
                    String.format("Withdrew $%,.2f successfully!\nNew balance: $%,.2f", amount, newBalance),
                    "Withdrawal Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public UserRepository getUserRepository(){
        return userRepository;
    }
    public AuthManager getAuthManager(){
        return authManager;
    }
    public CryptoManagerGUI getCryptoManagerGUI() {
        return cryptoManagerGUI;
    }

    public static CryptoManager getInstance() {
        if(instance == null) {
            instance = new CryptoManager();
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}