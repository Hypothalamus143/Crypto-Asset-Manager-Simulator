import java.util.Scanner;

public class AuthManager {
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);

    // Private constructor to prevent instantiation
    private AuthManager() {}

    public static User login() {
        return CryptoManagerGUI.showLoginGUI();
    }

    public static boolean createAccount() {
        return CryptoManagerGUI.showCreateAccountGUI();
    }

    // In AuthManager.logout()
    public static void logout() {
        if (currentUser != null) {
            UserRepository.saveUserData(currentUser, null);
            Sorter.resetToDefault();  // Reset sorting for next user
            System.out.println("Balance saved successfully.");
        }
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}