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
        mainFrame.setLocationRelativeTo(null);

        // Set mainPanel to landing panel
        mainPanel = createLandingPanel();
        mainFrame.add(mainPanel);
    }

    private void handleLogin() {
        User user = AuthManager.login();  // This calls the GUI login
        if (user != null) {
            cryptoManager.setCurrentUser(user);
            cryptoManager.runPortfolioManager();
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
    public void show() {
        mainFrame.setVisible(true);
    }
}