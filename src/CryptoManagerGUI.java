import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class CryptoManagerGUI {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private CryptoManager cryptoManager;
    private String currentSortBy = "Symbol";
    private String currentSortDirection = "Ascending";

    // Panel constants
    private static final String LANDING_PANEL = "LANDING";
    private static final String PORTFOLIO_PANEL = "PORTFOLIO";

    // Add instance variable
    private JPanel chartPanel;

    private JPanel chartContainer;
    private String currentChartSymbol = "BTC";

    public CryptoManagerGUI() {
        this.cryptoManager = new CryptoManager();
        initializeGUI();
    }

    private void initializeGUI() {
        // Create main frame with larger size
        mainFrame = new JFrame("Crypto Portfolio Manager");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 900); // Increased from 800x600 to 1000x700
        mainFrame.setLocationRelativeTo(null); // Center the window

        setupResizeListener();

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
        createAccDialog.setSize(400, 280);
        createAccDialog.setLocationRelativeTo(null);
        createAccDialog.setResizable(false);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create form panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Fixed width labels
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JLabel confirmLabel = new JLabel("Confirm Password:");

        // Set preferred size for labels to prevent width changes
        Dimension labelSize = new Dimension(120, 25);
        userLabel.setPreferredSize(labelSize);
        passLabel.setPreferredSize(labelSize);
        confirmLabel.setPreferredSize(labelSize);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JPasswordField confirmPassField = new JPasswordField(15);

        // Row 0: Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0; // Labels don't expand
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Fields expand
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(userField, gbc);

        // Row 1: Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passField, gbc);

        // Row 2: Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(confirmPassField, gbc);

        // Row 3: Password match indicator (centered, spans 2 columns)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 5, 5);

        JLabel passwordMatchLabel = new JLabel("", JLabel.CENTER);
        passwordMatchLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        passwordMatchLabel.setForeground(Color.GRAY);
        // Fixed height to prevent layout shifts
        passwordMatchLabel.setPreferredSize(new Dimension(0, 20));
        formPanel.add(passwordMatchLabel, gbc);

        // Add filler to push everything up
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(Box.createVerticalGlue(), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton createBtn = new JButton("Create Account");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        final boolean[] result = new boolean[1];

        // Real-time password matching indicator
        DocumentListener passwordListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkPasswords();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkPasswords();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkPasswords();
            }

            private void checkPasswords() {
                String password = new String(passField.getPassword());
                String confirmPassword = new String(confirmPassField.getPassword());

                if (password.isEmpty() && confirmPassword.isEmpty()) {
                    passwordMatchLabel.setText("");
                    passwordMatchLabel.setForeground(Color.GRAY);
                } else if (password.equals(confirmPassword)) {
                    passwordMatchLabel.setText("✓ Passwords match");
                    passwordMatchLabel.setForeground(new Color(0, 150, 0));
                } else {
                    passwordMatchLabel.setText("✗ Passwords do not match");
                    passwordMatchLabel.setForeground(Color.RED);
                }

                // Force layout to stay stable
                passwordMatchLabel.setPreferredSize(new Dimension(0, 20));
                createAccDialog.pack();
            }
        };

        passField.getDocument().addDocumentListener(passwordListener);
        confirmPassField.getDocument().addDocumentListener(passwordListener);

        createBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String confirmPassword = new String(confirmPassField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(createAccDialog,
                        "Please fill all fields",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.contains(",")) {
                JOptionPane.showMessageDialog(createAccDialog,
                        "Username cannot contain commas",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.contains(",")) {
                JOptionPane.showMessageDialog(createAccDialog,
                        "Password cannot contain commas",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(createAccDialog,
                        "Passwords do not match",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                passField.setText("");
                confirmPassField.setText("");
                passField.requestFocus();
                return;
            }

            if (UserRepository.userExists(username)) {
                JOptionPane.showMessageDialog(createAccDialog,
                        "Username already exists",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new user
            User newUser = new User(username);
            boolean success = UserRepository.saveUserData(newUser, password);

            if (success) {
                result[0] = true;
                createAccDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(createAccDialog,
                        "Error creating account",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> {
            createAccDialog.dispose();
        });

        // Enter key support
        userField.addActionListener(e -> passField.requestFocus());
        passField.addActionListener(e -> confirmPassField.requestFocus());
        confirmPassField.addActionListener(e -> createBtn.doClick());

        createAccDialog.add(mainPanel);
        createAccDialog.pack();
        createAccDialog.setVisible(true);

        return result[0];
    }
    private JPanel createPortfolioContent() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;

        // Summary panel - top
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // Doesn't expand vertically
        contentPanel.add(createSummaryPanel(), gbc);

        // Chart panel - middle
        gbc.gridy = 1;
        gbc.weighty = 0.3; // Takes 30% of vertical space
        chartPanel = createChartPanel();
        contentPanel.add(chartPanel, gbc);

        // Split pane - bottom
        gbc.gridy = 2;
        gbc.weighty = 0.7; // Takes 70% of vertical space
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createAssetsPanel());
        splitPane.setRightComponent(createMarketPricesPanel());
        splitPane.setDividerLocation(0.65); // Percentage instead of pixels
        contentPanel.add(splitPane, gbc);

        return contentPanel;
    }

    private JPanel createChartPanel() {
        chartContainer = new JPanel(new BorderLayout());
        updateChartContent(); // Initialize with current symbol
        return chartContainer;
    }

    private void updateChartContent() {
        chartContainer.removeAll();

        String symbol = currentChartSymbol;
        List<Double> priceHistory = MarketManager.getPriceHistory(symbol);

        if (priceHistory.isEmpty() || priceHistory.size() < 20) {
            //chartContainer.add(new JLabel("No price data available", JLabel.CENTER), BorderLayout.CENTER);
            for(int i = 0; i < 20; i++) {
                cryptoManager.checkMarket();
            }
            showPortfolioPanel(cryptoManager.getCurrentUser());
        } else {
            // Create the chart panel
            JPanel chartPanel = createSimpleChart(priceHistory, symbol);

            // Wrap it in a scroll pane with horizontal scrolling
            JScrollPane scrollPane = new JScrollPane(chartPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(20); // Smooth scrolling

            chartContainer.add(scrollPane, BorderLayout.CENTER);
        }

        // Buy button
        JButton buyButton = new JButton("Buy " + symbol);
        buyButton.addActionListener(e -> {
            double currentPrice = MarketManager.getCurrentPrice(symbol);
            handleBuy(symbol, currentPrice);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(buyButton);
        chartContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Update title
        chartContainer.setBorder(BorderFactory.createTitledBorder(
                MarketManager.getCryptoName(symbol) + " (" + symbol + ") Price Chart"));
        chartContainer.revalidate();
        chartContainer.repaint();
    }


    private JPanel createSimpleChart(List<Double> prices, String symbol) {
        // Create a chart panel that's wider than the viewport
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Pass this panel's dimensions to drawPriceChart
                drawPriceChart(g, prices, symbol, getWidth(), getHeight());
            }

            @Override
            public Dimension getPreferredSize() {
                // Make width proportional to number of data points
                int dataPoints = prices.size();
                int width = Math.max(800, dataPoints * 10); // At least 800px, or 10px per data point
                int height = Math.max(150, getParent() != null ? getParent().getHeight() / 3 : 150);
                return new Dimension(width, height);
            }
        };

        return chartPanel;
    }

    private void drawPriceChart(Graphics g, List<Double> prices, String symbol, int width, int height) {
        if (prices.size() < 2) return;

        // Use the full width of the chart panel (not the viewport)
        // This ensures all data points are visible when scrolling

        int padding = 30; // Increased padding for better labels

        // Find min and max prices for scaling
        double minPrice = Collections.min(prices);
        double maxPrice = Collections.max(prices);
        double priceRange = maxPrice - minPrice;

        // Set background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Draw grid lines and labels
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 4; i++) {
            int y = padding + (int)((height - 2 * padding) * (1 - (double)i / 4));
            g.drawLine(padding, y, width - padding, y);

            // Price labels on left
            double price = minPrice + (priceRange * i / 4);
            g.setColor(Color.BLACK);
            g.drawString(String.format("$%,.0f", price), 5, y + 4);
            g.setColor(Color.LIGHT_GRAY);
        }

        // Draw time markers on bottom (if we have enough data)
        if (prices.size() > 10) {
            g.setColor(Color.GRAY);
            int timeMarkers = Math.min(10, prices.size() - 1);
            for (int i = 0; i <= timeMarkers; i++) {
                int x = padding + (int)((width - 2 * padding) * ((double)i / timeMarkers));
                g.drawLine(x, height - padding, x, height - padding + 5);

                // Label every other marker to avoid clutter
                if (i % 2 == 0) {
                    g.setColor(Color.BLACK);
                    g.drawString("T-" + (timeMarkers - i), x - 10, height - padding + 20);
                    g.setColor(Color.GRAY);
                }
            }
        }

        // Draw price line with anti-aliasing for smoother lines
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(2.0f)); // Thicker line

        for (int i = 1; i < prices.size(); i++) {
            int x1 = padding + (int)((width - 2 * padding) * ((double)(i - 1) / (prices.size() - 1)));
            int y1 = padding + (int)((height - 2 * padding) * (1 - (prices.get(i - 1) - minPrice) / priceRange));

            int x2 = padding + (int)((width - 2 * padding) * ((double)i / (prices.size() - 1)));
            int y2 = padding + (int)((height - 2 * padding) * (1 - (prices.get(i) - minPrice) / priceRange));

            g2d.drawLine(x1, y1, x2, y2);

            // Draw data points for significant changes
            if (prices.size() > 20 && i % (prices.size() / 20) == 0 || i == prices.size() - 1) {
                g2d.setColor(Color.RED);
                g2d.fillOval(x2 - 3, y2 - 3, 6, 6);
                g2d.setColor(Color.BLUE);
            }
        }

        // Draw current price info
        double currentPrice = prices.get(prices.size() - 1);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Current Price: $" + String.format("%,.2f", currentPrice),
                width - 200, padding + 15);

        // Draw min/max labels
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("High: $" + String.format("%,.0f", maxPrice), width - 200, padding + 35);
        g.drawString("Low: $" + String.format("%,.0f", minPrice), width - 200, padding + 50);

        // Draw price change if we have enough data
        if (prices.size() > 1) {
            double firstPrice = prices.get(0);
            double priceChange = ((currentPrice - firstPrice) / firstPrice) * 100;
            Color changeColor = priceChange >= 0 ? Color.GREEN : Color.RED;
            g.setColor(changeColor);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(String.format("%+.2f%%", priceChange), width - 200, padding + 70);
        }
    }

    private JPanel createMarketPricesPanel() {
        JPanel marketPanel = new JPanel(new BorderLayout());
        marketPanel.setBorder(BorderFactory.createTitledBorder("Market Prices"));

        // Create a container panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Expand horizontally
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Take full width

        // Get current market prices
        Map<String, Double> marketPrices = MarketManager.getAllPrices();

        if (marketPrices.isEmpty()) {
            JLabel emptyLabel = new JLabel("No market data available", JLabel.CENTER);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(emptyLabel, gbc);
        } else {
            int row = 0;
            for (Map.Entry<String, Double> entry : marketPrices.entrySet()) {
                gbc.gridy = row++;
                JPanel pricePanel = createMarketPricePanel(entry.getKey(), entry.getValue());
                contentPanel.add(pricePanel, gbc);

                // Add vertical spacing
                gbc.gridy = row++;
                gbc.weighty = 0.0;
                contentPanel.add(Box.createVerticalStrut(5), gbc);
            }
        }

        // Add glue to push everything up
        gbc.gridy++;
        gbc.weighty = 1.0; // Push components up
        contentPanel.add(Box.createVerticalGlue(), gbc);

        // Add to scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add refresh button at the bottom
        JPanel bottomPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh Prices");
        refreshButton.addActionListener(e -> handleMarket());
        bottomPanel.add(refreshButton);

        marketPanel.add(scrollPane, BorderLayout.CENTER);
        marketPanel.add(bottomPanel, BorderLayout.SOUTH);

        return marketPanel;
    }

    private JPanel createMarketPricePanel(String symbol, double price) {
        // Create a panel that will fill width
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBorder(BorderFactory.createEtchedBorder());
        pricePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String assetName = MarketManager.getCryptoName(symbol);
        JLabel symbolLabel = new JLabel(assetName + " (" + symbol + ")");
        JLabel priceLabel = new JLabel("$" + String.format("%,.2f", price));

        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Add padding
        symbolLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        priceLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pricePanel.add(symbolLabel, BorderLayout.WEST);
        pricePanel.add(priceLabel, BorderLayout.EAST);

        // Create a mouse listener
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeChart(symbol);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                pricePanel.setBackground(new Color(200, 200, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pricePanel.setBackground(null);
            }
        };

        // Add to all components
        pricePanel.addMouseListener(mouseAdapter);
        symbolLabel.addMouseListener(mouseAdapter);
        priceLabel.addMouseListener(mouseAdapter);

        return pricePanel;
    }
    private void changeChart(String symbol) {
        currentChartSymbol = symbol;

        chartContainer.removeAll();

        List<Double> priceHistory = MarketManager.getPriceHistory(symbol);

        if (priceHistory.isEmpty()) {
            chartContainer.add(new JLabel("No price data available", JLabel.CENTER), BorderLayout.CENTER);
        } else {
            // Create a panel that will expand
            JPanel chartPanel = createSimpleChart(priceHistory, symbol);
            chartContainer.add(chartPanel, BorderLayout.CENTER);
        }

        // Update buy button
        JButton buyButton = new JButton("Buy " + symbol);
        buyButton.addActionListener(e -> {
            double currentPrice = MarketManager.getCurrentPrice(symbol);
            handleBuy(symbol, currentPrice);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(buyButton);
        chartContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Update title
        chartContainer.setBorder(BorderFactory.createTitledBorder(
                MarketManager.getCryptoName(symbol) + " (" + symbol + ") Price Chart"));

        chartContainer.revalidate();
        chartContainer.repaint();
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

    private double showBuyCryptoGUI(String symbol, double currentPrice, double currentBalance) {
        JDialog buyDialog = new JDialog(mainFrame, "Buy " + cryptoManager.getAssetName(symbol), true);
        buyDialog.setSize(400, 350);
        buyDialog.setLocationRelativeTo(mainFrame);
        buyDialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Info section
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setMaximumSize(new Dimension(380, 80));

        JLabel symbolLabel = new JLabel("Cryptocurrency:");
        JLabel symbolValue = new JLabel(cryptoManager.getAssetName(symbol) + " (" + symbol + ")");

        JLabel priceLabel = new JLabel("Current Price:");
        JLabel priceValue = new JLabel("$" + String.format("%,.2f", currentPrice));

        JLabel balanceLabel = new JLabel("Your Balance:");
        JLabel balanceValue = new JLabel("$" + String.format("%,.2f", currentBalance));

        infoPanel.add(symbolLabel);
        infoPanel.add(symbolValue);
        infoPanel.add(priceLabel);
        infoPanel.add(priceValue);
        infoPanel.add(balanceLabel);
        infoPanel.add(balanceValue);

        // Slider section
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Amount to Buy"));
        sliderPanel.setMaximumSize(new Dimension(380, 120));

        JSlider amountSlider = new JSlider(0, 100, 0); // 0% to 100%
        amountSlider.setMajorTickSpacing(25);
        amountSlider.setMinorTickSpacing(5);
        amountSlider.setPaintTicks(true);
        amountSlider.setPaintLabels(true);

        JLabel sliderValue = new JLabel("0% - $0.00", JLabel.CENTER);
        sliderValue.setFont(new Font("Arial", Font.BOLD, 12));

        // Manual input field
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField("0", 10);
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        sliderPanel.add(sliderValue);
        sliderPanel.add(Box.createVerticalStrut(5));
        sliderPanel.add(amountSlider);
        sliderPanel.add(Box.createVerticalStrut(5));
        sliderPanel.add(inputPanel);

        // Cost display
        JPanel costPanel = new JPanel();
        costPanel.setBorder(BorderFactory.createTitledBorder("Purchase Summary"));
        costPanel.setMaximumSize(new Dimension(380, 60));

        JLabel costLabel = new JLabel("Total Cost: $0.00", JLabel.CENTER);
        costLabel.setFont(new Font("Arial", Font.BOLD, 14));
        costPanel.add(costLabel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton buyBtn = new JButton("Buy");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(buyBtn);
        buttonPanel.add(cancelBtn);

        // Add all panels
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sliderPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(costPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        final double[] result = new double[]{-1}; // -1 means cancelled

        // Update function
        Runnable updateValues = () -> {
            try {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    amountField.setText("0");
                    return;
                }

                double amount = Double.parseDouble(amountText);

                // Handle very small amounts that might round to 0
                if (amount > 0 && amount < 0.000001) {
                    amount = 0.000001;
                    amountField.setText(String.format("%.6f", amount));
                }

                double totalCost = amount * currentPrice;
                double percentage = (totalCost / currentBalance) * 100;

                // Update labels
                sliderValue.setText(String.format("%.1f%% - $%,.2f", percentage, totalCost));
                costLabel.setText(String.format("Total Cost: $%,.2f", totalCost));

                // Color code based on affordability
                if (totalCost > currentBalance) {
                    costLabel.setForeground(Color.RED);
                    buyBtn.setEnabled(false);
                } else {
                    costLabel.setForeground(Color.BLACK);
                    buyBtn.setEnabled(true);
                }

            } catch (NumberFormatException ex) {
                // If we get an error, reset to 0
                amountField.setText("0");
                costLabel.setText("Total Cost: $0.00");
                costLabel.setForeground(Color.BLACK);
                buyBtn.setEnabled(false);
            }
        };

        // Slider listener
        amountSlider.addChangeListener(e -> {
            if (!amountSlider.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(() -> {
                    double percentage = amountSlider.getValue();

                    // Ensure we have a minimum viable amount when percentage > 0
                    if (percentage > 0) {
                        double totalCost = currentBalance * (percentage / 100.0);
                        double amount = totalCost / currentPrice;

                        // Ensure minimum amount to avoid rounding to 0
                        if (amount < 0.000001) {
                            amount = 0.000001;
                        }

                        amountField.setText(String.format("%.6f", amount));
                    } else {
                        amountField.setText("0");
                    }
                    updateValues.run();
                });
            }
        });

        // Text field listener
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateValues.run());
            }
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateValues.run());
            }
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateValues.run());
            }
        });

        buyBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(buyDialog, "Amount must be positive");
                    return;
                }

                double totalCost = amount * currentPrice;
                if (totalCost > currentBalance) {
                    JOptionPane.showMessageDialog(buyDialog,
                            String.format("Insufficient funds. You need $%,.2f", totalCost));
                    return;
                }

                result[0] = amount;
                buyDialog.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(buyDialog, "Please enter a valid amount");
            }
        });

        cancelBtn.addActionListener(e -> {
            buyDialog.dispose();
        });

        // Initialize
        updateValues.run();

        buyDialog.add(panel);
        buyDialog.pack();
        buyDialog.setVisible(true);

        return result[0];
    }

    private JPanel createAssetsPanel() {
        JPanel assetsContainer = new JPanel(new BorderLayout());
        assetsContainer.setBorder(BorderFactory.createTitledBorder("Your Assets"));

        // Create sorting controls panel
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Sort by dropdown
        String[] sortOptions = {"Symbol", "Total Value", "Profit Amount", "Profit Percentage", "Buy Price", "Amount"};
        JComboBox<String> sortByCombo = new JComboBox<>(sortOptions);
        sortByCombo.setSelectedItem(currentSortBy);

        // Sort direction dropdown
        String[] directionOptions = {"Ascending", "Descending"};
        JComboBox<String> directionCombo = new JComboBox<>(directionOptions);
        directionCombo.setSelectedItem(currentSortDirection);

        sortPanel.add(new JLabel("Sort by:"));
        sortPanel.add(sortByCombo);
        sortPanel.add(new JLabel("Order:"));
        sortPanel.add(directionCombo);

        // Assets list panel
        JPanel assetsListPanel = new JPanel();
        assetsListPanel.setLayout(new BoxLayout(assetsListPanel, BoxLayout.Y_AXIS));

        // Populate assets
        refreshAssetsList(assetsListPanel);

        // Create scroll pane - disable horizontal scrolling
        JScrollPane scrollPane = new JScrollPane(assetsListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scroll

        // Prevent horizontal expansion
        assetsListPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        // Add action listeners for auto-sort
        ActionListener sortListener = e -> {
            handleSort((String) sortByCombo.getSelectedItem(),
                    (String) directionCombo.getSelectedItem());
        };

        sortByCombo.addActionListener(sortListener);
        directionCombo.addActionListener(sortListener);

        assetsContainer.add(sortPanel, BorderLayout.NORTH);
        assetsContainer.add(scrollPane, BorderLayout.CENTER);

        return assetsContainer;
    }
    private JPanel createActionButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");

        // Add action listeners (to be implemented)
        depositButton.addActionListener(e -> handleDeposit());
        withdrawButton.addActionListener(e -> handleWithdraw());

        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);

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
        assetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        assetPanel.setPreferredSize(new Dimension(480, 60));
        assetPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        double currentPrice = asset.getCurrentPrice();
        double unrealizedProfit = asset.getUnrealizedProfit();
        double profitPercentage = (currentPrice - asset.getBuyPrice()) / asset.getBuyPrice() * 100;

        // Main info
        JLabel mainInfo = new JLabel(String.format("%.6f %s | Buy Price: $%,.2f",
                asset.getAmount(), asset.getSymbol(), asset.getBuyPrice()));
        mainInfo.setFont(new Font("Arial", Font.BOLD, 12));

        // Profit/Loss info
        JLabel plInfo = new JLabel(String.format("Current Value: $%,.2f | P/L: $%,.2f (%.2f%%)",
                asset.getTotalValue(), unrealizedProfit, profitPercentage));

        // Color code based on profit/loss
        if (unrealizedProfit >= 0) {
            plInfo.setForeground(Color.GREEN);
        } else {
            plInfo.setForeground(Color.RED);
        }

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false); // Make this panel transparent
        infoPanel.add(mainInfo);
        infoPanel.add(plInfo);

        // Make labels non-opaque so background shows through
        mainInfo.setOpaque(false);
        plInfo.setOpaque(false);

        assetPanel.add(infoPanel, BorderLayout.CENTER);

        // Create a mouse listener that will work for the entire panel
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSell(asset);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                assetPanel.setBackground(new Color(200, 200, 200)); // Darker gray
            }

            @Override
            public void mouseExited(MouseEvent e) {
                assetPanel.setBackground(null);
            }
        };

        // Add the mouse listener to both the main panel and the info panel
        assetPanel.addMouseListener(mouseAdapter);
        infoPanel.addMouseListener(mouseAdapter);

        // Also add to the labels to ensure full coverage
        mainInfo.addMouseListener(mouseAdapter);
        plInfo.addMouseListener(mouseAdapter);

        return assetPanel;
    }

    private void handleLogout() {
        AuthManager.logout();
        cryptoManager.setCurrentUser(null);
        showLandingPanel();
    }

    // Stub methods for actions (to be implemented)
    private void handleBuy(String symbol, double currentPrice) {
        double amount = showBuyCryptoGUI(symbol, currentPrice, cryptoManager.getCurrentUser().getBalance());

        if (amount > 0) {
            // Call the modified terminal method with parameters
            cryptoManager.buyCrypto(symbol, currentPrice, amount);
            showPortfolioPanel(cryptoManager.getCurrentUser()); // Refresh portfolio
        }
    }

    private void handleSell(Asset asset) {
        double amount = showSellCryptoGUI(asset);

        if (amount > 0) {
            // Call the modified terminal method with parameters
            cryptoManager.sellCrypto(asset, amount);
            showPortfolioPanel(cryptoManager.getCurrentUser()); // Refresh portfolio
        }
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
        showPortfolioPanel(cryptoManager.getCurrentUser());
    }

    private void handleSort(String sortBy, String direction) {
        currentSortBy = sortBy;
        currentSortDirection = direction;

        cryptoManager.sortLots(sortBy, direction);
        showPortfolioPanel(cryptoManager.getCurrentUser());
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

    private double showSellCryptoGUI(Asset asset) {
        JDialog sellDialog = new JDialog(mainFrame, "Sell " + asset.getSymbol(), true);
        sellDialog.setSize(400, 350);
        sellDialog.setLocationRelativeTo(mainFrame);
        sellDialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        double currentPrice = asset.getCurrentPrice();

        // Info section
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        infoPanel.setMaximumSize(new Dimension(380, 100));

        JLabel symbolLabel = new JLabel("Cryptocurrency:");
        JLabel symbolValue = new JLabel(asset.getName() + " (" + asset.getSymbol() + ")");

        JLabel priceLabel = new JLabel("Current Price:");
        JLabel priceValue = new JLabel("$" + String.format("%,.2f", currentPrice));

        JLabel buyPriceLabel = new JLabel("Your Buy Price:");
        JLabel buyPriceValue = new JLabel("$" + String.format("%,.2f", asset.getBuyPrice()));

        JLabel ownedLabel = new JLabel("You Own:");
        JLabel ownedValue = new JLabel(String.format("%.6f %s", asset.getAmount(), asset.getSymbol()));

        infoPanel.add(symbolLabel);
        infoPanel.add(symbolValue);
        infoPanel.add(priceLabel);
        infoPanel.add(priceValue);
        infoPanel.add(buyPriceLabel);
        infoPanel.add(buyPriceValue);
        infoPanel.add(ownedLabel);
        infoPanel.add(ownedValue);

        // Slider section
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Amount to Sell"));
        sliderPanel.setMaximumSize(new Dimension(380, 120));

        JSlider amountSlider = new JSlider(0, 100, 0); // 0% to 100%
        amountSlider.setMajorTickSpacing(25);
        amountSlider.setMinorTickSpacing(5);
        amountSlider.setPaintTicks(true);
        amountSlider.setPaintLabels(true);

        JLabel sliderValue = new JLabel("0% - 0.000000 " + asset.getSymbol(), JLabel.CENTER);
        sliderValue.setFont(new Font("Arial", Font.BOLD, 12));

        // Manual input field
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField("0", 10);
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        sliderPanel.add(sliderValue);
        sliderPanel.add(Box.createVerticalStrut(5));
        sliderPanel.add(amountSlider);
        sliderPanel.add(Box.createVerticalStrut(5));
        sliderPanel.add(inputPanel);

        // Profit display
        JPanel profitPanel = new JPanel();
        profitPanel.setBorder(BorderFactory.createTitledBorder("Sale Summary"));
        profitPanel.setMaximumSize(new Dimension(380, 80));

        JLabel proceedsLabel = new JLabel("You Receive: $0.00", JLabel.CENTER);
        JLabel profitLabel = new JLabel("Profit: $0.00", JLabel.CENTER);
        proceedsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        profitLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel profitLabels = new JPanel(new GridLayout(2, 1));
        profitLabels.add(proceedsLabel);
        profitLabels.add(profitLabel);
        profitPanel.add(profitLabels);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton sellBtn = new JButton("Sell");
        JButton cancelBtn = new JButton("Cancel");
        buttonPanel.add(sellBtn);
        buttonPanel.add(cancelBtn);

        // Add all panels
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sliderPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(profitPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buttonPanel);

        final double[] result = new double[]{-1}; // -1 means cancelled

        // Update function
        Runnable updateValues = () -> {
            try {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    amountField.setText("0");
                    return;
                }

                double amount = Double.parseDouble(amountText);

                // Handle very small amounts
                if (amount > 0 && amount < 0.000001) {
                    amount = 0.000001;
                    amountField.setText(String.format("%.6f", amount));
                }

                // Ensure we don't exceed owned amount
                if (amount > asset.getAmount()) {
                    amount = asset.getAmount();
                    amountField.setText(String.format("%.6f", amount));
                }

                double totalValue = amount * currentPrice;
                double realizedProfit = (currentPrice - asset.getBuyPrice()) * amount;
                double percentage = (amount / asset.getAmount()) * 100;

                // Update labels
                sliderValue.setText(String.format("%.1f%% - %.6f %s", percentage, amount, asset.getSymbol()));
                proceedsLabel.setText(String.format("You Receive: $%,.2f", totalValue));
                profitLabel.setText(String.format("Profit: $%,.2f", realizedProfit));

                // Color code profit
                if (realizedProfit >= 0) {
                    profitLabel.setForeground(Color.GREEN);
                } else {
                    profitLabel.setForeground(Color.RED);
                }

                sellBtn.setEnabled(amount > 0);

            } catch (NumberFormatException ex) {
                amountField.setText("0");
                proceedsLabel.setText("You Receive: $0.00");
                profitLabel.setText("Profit: $0.00");
                profitLabel.setForeground(Color.BLACK);
                sellBtn.setEnabled(false);
            }
        };

        // Slider listener
        amountSlider.addChangeListener(e -> {
            if (!amountSlider.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(() -> {
                    double percentage = amountSlider.getValue();
                    double amount = asset.getAmount() * (percentage / 100.0);

                    // Ensure minimum amount
                    if (percentage > 0 && amount < 0.000001) {
                        amount = 0.000001;
                    }

                    amountField.setText(String.format("%.6f", amount));
                    updateValues.run();
                });
            }
        });

        // Text field listener
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateValues.run());
            }
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateValues.run());
            }
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateValues.run());
            }
        });

        sellBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(sellDialog, "Amount must be positive");
                    return;
                }

                if (amount > asset.getAmount()) {
                    JOptionPane.showMessageDialog(sellDialog,
                            String.format("You only own %.6f %s", asset.getAmount(), asset.getSymbol()));
                    return;
                }

                // Confirm sale
                double totalValue = amount * currentPrice;
                double realizedProfit = (currentPrice - asset.getBuyPrice()) * amount;

                int confirm = JOptionPane.showConfirmDialog(sellDialog,
                        String.format("Sale Summary:\nAsset: %s (%s)\nAmount: %.6f\nSell Price: $%,.2f\nYou Receive: $%,.2f\nProfit: $%,.2f\n\nConfirm sale?",
                                asset.getName(), asset.getSymbol(), amount, currentPrice, totalValue, realizedProfit),
                        "Confirm Sale",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    result[0] = amount;
                    sellDialog.dispose();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(sellDialog, "Please enter a valid amount");
            }
        });

        cancelBtn.addActionListener(e -> {
            sellDialog.dispose();
        });

        // Initialize
        updateValues.run();

        sellDialog.add(panel);
        sellDialog.pack();
        sellDialog.setVisible(true);

        return result[0];
    }
    private void setupResizeListener() {
        mainFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Force repaint of chart when window is resized
                if (chartContainer != null) {
                    chartContainer.revalidate();
                    chartContainer.repaint();
                }
            }
        });
    }

    public void show() {
        mainFrame.setVisible(true);
    }
}