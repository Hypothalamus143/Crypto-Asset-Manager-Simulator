import java.util.Scanner;

public class AuthManager {
    private static User currentUser;
    private static AuthManager instance;
    private CryptoManagerGUI cryptoManagerGUI;
    private UserRepository userRepository = UserRepository.getInstance();
    private Scanner scanner = new Scanner(System.in);

    // Private constructor to prevent instantiation
    private AuthManager() {
        cryptoManagerGUI = CryptoManagerGUI.getInstance();
    }

    public User login() {
        return cryptoManagerGUI.showLoginGUI();
    }

    public boolean createAccount() {
        return cryptoManagerGUI.showCreateAccountGUI();
    }

    // In AuthManager.logout()
    public void logout() {
        if (currentUser != null) {
            userRepository.saveUserData(currentUser, null);
            Sorter.resetToDefault();  // Reset sorting for next user
            System.out.println("Balance saved successfully.");
        }
        currentUser = null;
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