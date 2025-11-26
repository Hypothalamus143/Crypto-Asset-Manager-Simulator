import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CryptoManagerGUI {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private CryptoManager cryptoManager;

    public CryptoManagerGUI() {
        this.cryptoManager = new CryptoManager();
        initializeGUI();
    }

    private void initializeGUI() {
        // Create main frame
        mainFrame = new JFrame("Crypto Portfolio Manager");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null); // Center the window

        // Create main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add title
        JLabel titleLabel = new JLabel("Crypto Portfolio Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Create Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Create Create Account button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setFont(new Font("Arial", Font.PLAIN, 16));
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateAccount();
            }
        });

        // Add buttons to panel
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);

        // Add button panel to main panel
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add main panel to frame
        mainFrame.add(mainPanel);
    }

    private void handleLogin() {
        // Hide the GUI and run terminal login
        mainFrame.setVisible(false);

        // Call the AuthManager.login() method directly
        User user = AuthManager.login();
        if (user != null) {
            // Login successful - run the portfolio manager
            cryptoManager.setCurrentUser(user);
            cryptoManager.runPortfolioManager();
        }

        // After terminal session is done, show the GUI again
        mainFrame.setVisible(true);
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

    public void show() {
        mainFrame.setVisible(true);
    }
}