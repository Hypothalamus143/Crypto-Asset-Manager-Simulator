import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class CryptoManagerGUI {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private CryptoManager cryptoManager;

    // Panel constants
    private static final String LANDING_PANEL = "LANDING";
    private static final String PORTFOLIO_PANEL = "PORTFOLIO";

    public CryptoManagerGUI() {
        this.cryptoManager = new CryptoManager();
        initializeGUI();
    }

    private void initializeGUI() {
        mainFrame = new JFrame("Crypto Portfolio Manager");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600); // Larger for portfolio
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create and add panels
        mainPanel.add(createLandingPanel(), LANDING_PANEL);
        // Portfolio panel will be created on demand

        mainFrame.add(mainPanel);
        showLandingPanel();
    }

    private void handleLogin() {
        User user = AuthManager.login();
        if (user != null) {
            cryptoManager.setCurrentUser(user);
            showPortfolioPanel(user); // Switch to portfolio panel
        }
    }


    private void handleCreateAccount() {
        // Hide the GUI and run terminal account creation
        mainFrame.setVisible(false);

        // Call the AuthManager.createAccount() method directly
        boolean success = AuthManager.createAccount();
        if (success) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Account created successfully! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Show the GUI again
        mainFrame.setVisible(true);
    }


    private JPanel createLandingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title
        JLabel titleLabel = new JLabel("Crypto Portfolio Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Create Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loginButton.addActionListener(e -> handleLogin());

        // Create Create Account button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setFont(new Font("Arial", Font.PLAIN, 16));
        createAccountButton.addActionListener(e -> handleCreateAccount());

        // Add buttons to panel
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }


    public static User showLoginGUI() {
        JDialog loginDialog = new JDialog((JFrame)null, "Login", true); // Modal dialog
        loginDialog.setSize(300, 200);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginBtn);
        panel.add(cancelBtn);

        final User[] result = new User[1]; // Array to store result

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog, "Please fill all fields");
                return;
            }

            if (UserRepository.validateCredentials(username, password)) {
                User user = UserRepository.loadUser(username);
                if (user != null) {
                    result[0] = user;
                    loginDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Error loading user data");
                }
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Invalid username or password");
            }
        });

        cancelBtn.addActionListener(e -> {
            loginDialog.dispose();
        });

        // Enter key support
        passField.addActionListener(e -> loginBtn.doClick());

        loginDialog.add(panel);
        loginDialog.pack();
        loginDialog.setVisible(true); // This blocks until dialog is disposed

        return result[0];
    }

    public static boolean showCreateAccountGUI() {
        JDialog createAccDialog = new JDialog((JFrame)null, "Create Account", true);
        createAccDialog.setSize(350, 200);
        createAccDialog.setLocationRelativeTo(null);
        createAccDialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton createBtn = new JButton("Create Account");
        JButton cancelBtn = new JButton("Cancel");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(createBtn);
        panel.add(cancelBtn);

        final boolean[] result = new boolean[1]; // Array to store result

        createBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(createAccDialog, "Please fill all fields");
                return;
            }

            if (username.contains(",")) {
                JOptionPane.showMessageDialog(createAccDialog, "Username cannot contain commas");
                return;
            }

            if (password.contains(",")) {
                JOptionPane.showMessageDialog(createAccDialog, "Password cannot contain commas");
                return;
            }

            if (UserRepository.userExists(username)) {
                JOptionPane.showMessageDialog(createAccDialog, "Username already exists");
                return;
            }

            // Create new user
            User newUser = new User(username);
            boolean success = UserRepository.saveUserData(newUser, password);

            if (success) {
                result[0] = true;
                createAccDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(createAccDialog, "Error creating account");
            }
        });

        cancelBtn.addActionListener(e -> {
            createAccDialog.dispose();
        });

        // Enter key support
        passField.addActionListener(e -> createBtn.doClick());

        createAccDialog.add(panel);
        createAccDialog.pack();
        createAccDialog.setVisible(true); // This blocks until dialog is disposed

        return result[0];
    }
    private JPanel createPortfolioPanel() {
        JPanel portfolioPanel = new JPanel(new BorderLayout(10, 10));
        portfolioPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with user info and logout
        portfolioPanel.add(createPortfolioHeader(), BorderLayout.NORTH);

        // Main content - portfolio summary and assets
        portfolioPanel.add(createPortfolioContent(), BorderLayout.CENTER);

        // Action buttons at bottom
        portfolioPanel.add(createActionButtons(), BorderLayout.SOUTH);

        return portfolioPanel;
    }

    private JPanel createMarketPricesPanel() {
        JPanel marketPanel = new JPanel();
        marketPanel.setLayout(new BoxLayout(marketPanel, BoxLayout.Y_AXIS));
        marketPanel.setBorder(BorderFactory.createTitledBorder("Market Prices"));
        marketPanel.setPreferredSize(new Dimension(250, 300));

        // Get current market prices
        Map<String, Double> marketPrices = MarketManager.getAllPrices();

        if (marketPrices.isEmpty()) {
            JLabel emptyLabel = new JLabel("No market data available");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            marketPanel.add(emptyLabel);
        } else {
            for (Map.Entry<String, Double> entry : marketPrices.entrySet()) {
                marketPanel.add(createMarketPricePanel(entry.getKey(), entry.getValue()));
            }
        }

        return marketPanel;
    }

    private JPanel createMarketPricePanel(String symbol, double price) {
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pricePanel.setMaximumSize(new Dimension(230, 40));

        String assetName = cryptoManager.getAssetName(symbol);
        JLabel symbolLabel = new JLabel(assetName + " (" + symbol + ")");
        JLabel priceLabel = new JLabel("$" + String.format("%,.2f", price));

        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));

        pricePanel.add(symbolLabel, BorderLayout.WEST);
        pricePanel.add(priceLabel, BorderLayout.EAST);

        return pricePanel;
    }

    private JPanel createPortfolioHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + cryptoManager.getCurrentUser().getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createPortfolioContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        // Portfolio summary
        contentPanel.add(createSummaryPanel(), BorderLayout.NORTH);

        // Main content area - split between assets and market prices
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createAssetsPanel());
        splitPane.setRightComponent(createMarketPricesPanel());
        splitPane.setDividerLocation(500); // Adjust based on your preference

        contentPanel.add(splitPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Portfolio Summary"));

        User user = cryptoManager.getCurrentUser();

        // Calculate total portfolio value
        double totalValue = user.getBalance();
        for (Asset asset : user.getAssets()) {
            totalValue += asset.getTotalValue();
        }

        JLabel balanceLabel = new JLabel("Balance: $" + String.format("%,.2f", user.getBalance()));
        JLabel netProfitLabel = new JLabel("Net Profit: $" + String.format("%,.2f", user.getNetProfit()));
        JLabel realizedProfitLabel = new JLabel("Realized: $" + String.format("%,.2f", user.getRealizedProfit()));
        JLabel totalValueLabel = new JLabel("Total Value: $" + String.format("%,.2f", totalValue));

        // Style the labels
        Font boldFont = new Font("Arial", Font.BOLD, 14);
        balanceLabel.setFont(boldFont);
        totalValueLabel.setFont(boldFont);

        summaryPanel.add(balanceLabel);
        summaryPanel.add(netProfitLabel);
        summaryPanel.add(realizedProfitLabel);
        summaryPanel.add(totalValueLabel);
        summaryPanel.add(new JLabel()); // Empty cell
        summaryPanel.add(new JLabel()); // Empty cell

        return summaryPanel;
    }

    private JScrollPane createAssetsPanel() {
        JPanel assetsPanel = new JPanel();
        assetsPanel.setLayout(new BoxLayout(assetsPanel, BoxLayout.Y_AXIS));
        assetsPanel.setBorder(BorderFactory.createTitledBorder("Your Assets"));

        List<Asset> assets = cryptoManager.getCurrentUser().getAssets();

        if (assets.isEmpty()) {
            JLabel emptyLabel = new JLabel("No assets yet. Click 'Buy Crypto' to get started!");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            assetsPanel.add(emptyLabel);
        } else {
            for (Asset asset : assets) {
                assetsPanel.add(createAssetPanel(asset));
            }
        }

        JScrollPane scrollPane = new JScrollPane(assetsPanel);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        return scrollPane;
    }

    private JPanel createActionButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton buyButton = new JButton("Buy Crypto");
        JButton sellButton = new JButton("Sell Crypto");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton marketButton = new JButton("Market Prices");
        JButton sortButton = new JButton("Sort Assets");

        // Add action listeners (to be implemented)
        buyButton.addActionListener(e -> handleBuy());
        sellButton.addActionListener(e -> handleSell());
        depositButton.addActionListener(e -> handleDeposit());
        withdrawButton.addActionListener(e -> handleWithdraw());
        marketButton.addActionListener(e -> handleMarket());
        sortButton.addActionListener(e -> handleSort());

        buttonPanel.add(buyButton);
        buttonPanel.add(sellButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(marketButton);
        buttonPanel.add(sortButton);

        return buttonPanel;
    }

    private void refreshAssetsList(JPanel assetsPanel) {
        assetsPanel.removeAll();

        List<Asset> assets = cryptoManager.getCurrentUser().getAssets();

        if (assets.isEmpty()) {
            JLabel emptyLabel = new JLabel("No assets yet. Click 'Buy Crypto' to get started!");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            assetsPanel.add(emptyLabel);
        } else {
            for (Asset asset : assets) {
                assetsPanel.add(createAssetPanel(asset));
            }
        }

        assetsPanel.revalidate();
        assetsPanel.repaint();
    }

    private JPanel createAssetPanel(Asset asset) {
        JPanel assetPanel = new JPanel(new BorderLayout());
        assetPanel.setBorder(BorderFactory.createEtchedBorder());
        assetPanel.setMaximumSize(new Dimension(700, 80));

        double currentPrice = asset.getCurrentPrice();
        double unrealizedProfit = asset.getUnrealizedProfit();
        double profitPercentage = (currentPrice - asset.getBuyPrice()) / asset.getBuyPrice() * 100;

        // Main info
        JLabel mainInfo = new JLabel(String.format("%.6f %s | Buy: $%,.2f | Current: $%,.2f",
                asset.getAmount(), asset.getSymbol(), asset.getBuyPrice(), currentPrice));

        // Profit/Loss info
        JLabel plInfo = new JLabel(String.format("Value: $%,.2f | P/L: $%,.2f (%.2f%%)",
                asset.getTotalValue(), unrealizedProfit, profitPercentage));

        // Color code based on profit/loss
        if (unrealizedProfit >= 0) {
            plInfo.setForeground(Color.GREEN);
        } else {
            plInfo.setForeground(Color.RED);
        }

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(mainInfo);
        infoPanel.add(plInfo);

        assetPanel.add(infoPanel, BorderLayout.CENTER);

        return assetPanel;
    }

    private void handleLogout() {
        AuthManager.logout();
        cryptoManager.setCurrentUser(null);
        showLandingPanel();
    }

    // Stub methods for actions (to be implemented)
    private void handleBuy() {
        mainFrame.setVisible(false);
        cryptoManager.buyCrypto(); // Call existing terminal method
        mainFrame.setVisible(true);
        showPortfolioPanel(cryptoManager.getCurrentUser()); // Refresh with new data
    }

    private void handleSell() {
        mainFrame.setVisible(false);
        cryptoManager.sellCrypto(); // Call existing terminal method
        mainFrame.setVisible(true);
        showPortfolioPanel(cryptoManager.getCurrentUser()); // Refresh with new data
    }

    private void handleDeposit() {
        cryptoManager.deposit(); // This now calls the GUI internally
        showPortfolioPanel(cryptoManager.getCurrentUser()); // Refresh with new balance
    }

    private void handleWithdraw() {
        cryptoManager.withdraw(); // This now calls the GUI internally
        showPortfolioPanel(cryptoManager.getCurrentUser()); // Refresh with new balance
    }

    private void handleMarket() {
        cryptoManager.checkMarket(); // Call existing terminal method
        mainFrame.setVisible(true);
        // No need to refresh portfolio since market check doesn't change user data
    }

    private void handleSort() {
        mainFrame.setVisible(false);
        cryptoManager.sortLots(); // Call existing terminal method
        mainFrame.setVisible(true);
        showPortfolioPanel(cryptoManager.getCurrentUser()); // Refresh with sorted data
    }

    public void showLandingPanel() {
        cardLayout.show(mainPanel, LANDING_PANEL);
    }

    public void showPortfolioPanel(User user) {
        // Always create a fresh portfolio panel
        JPanel portfolioPanel = createPortfolioPanel();

        // Remove existing portfolio panel if any
        Component[] comps = mainPanel.getComponents();
        for (Component comp : comps) {
            if (comp.getName() != null && comp.getName().equals(PORTFOLIO_PANEL)) {
                mainPanel.remove(comp);
            }
        }

        portfolioPanel.setName(PORTFOLIO_PANEL);
        mainPanel.add(portfolioPanel, PORTFOLIO_PANEL);
        cardLayout.show(mainPanel, PORTFOLIO_PANEL);

        // No refresh needed - panel is created fresh with latest data
    }
    public static double showWithdrawGUI(double currentBalance) {
        JDialog withdrawDialog = new JDialog((JFrame)null, "Withdraw Funds", true);
        withdrawDialog.setSize(300, 200);
        withdrawDialog.setLocationRelativeTo(null);
        withdrawDialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel balanceLabel = new JLabel("Current Balance:");
        JLabel balanceValue = new JLabel("$" + String.format("%,.2f", currentBalance));
        JLabel amountLabel = new JLabel("Withdraw Amount:");
        JTextField amountField = new JTextField();

        JButton withdrawBtn = new JButton("Withdraw");
        JButton cancelBtn = new JButton("Cancel");

        panel.add(balanceLabel);
        panel.add(balanceValue);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(withdrawBtn);
        panel.add(cancelBtn);

        final double[] result = new double[]{-1}; // -1 means cancelled

        withdrawBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(withdrawDialog, "Amount must be positive");
                    return;
                }

                if (amount > currentBalance) {
                    JOptionPane.showMessageDialog(withdrawDialog,
                            String.format("Insufficient funds. You have $%,.2f", currentBalance));
                    return;
                }

                result[0] = amount;
                withdrawDialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(withdrawDialog, "Please enter a valid amount");
            }
        });

        cancelBtn.addActionListener(e -> {
            withdrawDialog.dispose();
        });

        // Enter key support
        amountField.addActionListener(e -> withdrawBtn.doClick());

        withdrawDialog.add(panel);
        withdrawDialog.pack();
        withdrawDialog.setVisible(true);

        return result[0];
    }

    public static double showDepositGUI(double currentBalance) {
        JDialog depositDialog = new JDialog((JFrame)null, "Deposit Funds", true);
        depositDialog.setSize(300, 200);
        depositDialog.setLocationRelativeTo(null);
        depositDialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel balanceLabel = new JLabel("Current Balance:");
        JLabel balanceValue = new JLabel("$" + String.format("%,.2f", currentBalance));
        JLabel amountLabel = new JLabel("Deposit Amount:");
        JTextField amountField = new JTextField();

        JButton depositBtn = new JButton("Deposit");
        JButton cancelBtn = new JButton("Cancel");

        panel.add(balanceLabel);
        panel.add(balanceValue);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(depositBtn);
        panel.add(cancelBtn);

        final double[] result = new double[]{-1}; // -1 means cancelled

        depositBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(depositDialog, "Amount must be positive");
                    return;
                }

                if (amount > 1000000) {
                    JOptionPane.showMessageDialog(depositDialog, "Deposit amount cannot exceed $1,000,000");
                    return;
                }

                result[0] = amount;
                depositDialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(depositDialog, "Please enter a valid amount");
            }
        });

        cancelBtn.addActionListener(e -> {
            depositDialog.dispose();
        });

        // Enter key support
        amountField.addActionListener(e -> depositBtn.doClick());

        depositDialog.add(panel);
        depositDialog.pack();
        depositDialog.setVisible(true);

        return result[0];
    }
    public void show() {
        mainFrame.setVisible(true);
    }
}