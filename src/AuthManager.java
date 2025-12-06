import javax.swing.*;
import java.util.Scanner;
public class AuthManager {
    private static User currentUser;
    private static AuthManager instance;
    private CryptoManagerGUI cryptoManagerGUI;
    private UserRepository userRepository = UserRepository.getInstance();
    private Scanner scanner = new Scanner(System.in);

    // Private constructor to prevent instantiation
    private AuthManager() {

    }

    public User login(LoginAttempt loginAttempt) {
        if (loginAttempt == null || !loginAttempt.isValid()) {
            JOptionPane.showMessageDialog(null,
                    "Please fill all fields",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String username = loginAttempt.getUsername();
        String password = loginAttempt.getPassword();

        // Validate credentials
        if (userRepository.validateCredentials(username, password)) {
            User user = userRepository.loadUser(username);
            if (user != null) {
                System.out.println("Login successful for: " + username);
                return user;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Error loading user data",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    public boolean createAccount(CreateAccountRequest request) {
        if (request == null || !request.isValid()) {
            JOptionPane.showMessageDialog(null,
                    "Please fill all fields",
                    "Create Account Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String username = request.getUsername();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        // Password match validation
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null,
                    "Passwords do not match",
                    "Create Account Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Username validation
        if (username.contains(",")) {
            JOptionPane.showMessageDialog(null,
                    "Username cannot contain commas",
                    "Create Account Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Password validation
        if (password.contains(",")) {
            JOptionPane.showMessageDialog(null,
                    "Password cannot contain commas",
                    "Create Account Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check if user already exists
        if (userRepository.userExists(username)) {
            JOptionPane.showMessageDialog(null,
                    "Username already exists",
                    "Create Account Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Create new user
        User newUser = new User(username);
        boolean success = userRepository.saveUserData(newUser, password);

        if (success) {
            System.out.println("Account created successfully for: " + username);

            // Show success message
            JOptionPane.showMessageDialog(null,
                    "Account created successfully!\n\nUsername: " + username + "\n\nYou can now login with your new account.",
                    "Account Created Successfully",
                    JOptionPane.INFORMATION_MESSAGE);

            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Error creating account. Please try again.",
                    "Create Account Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static AuthManager getInstance() {
        if(instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}