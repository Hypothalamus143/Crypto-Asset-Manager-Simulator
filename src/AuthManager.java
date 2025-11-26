import java.util.Scanner;

public class AuthManager {
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);

    // Private constructor to prevent instantiation
    private AuthManager() {}

    public static User login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (UserRepository.validateCredentials(username, password)) {
            User user = UserRepository.loadUser(username);
            if (user != null) {
                currentUser = user;
                System.out.println("Login successful! Welcome back, " + username + "!");
                return user;
            } else {
                System.out.println("Error loading user data.");
            }
        } else {
            System.out.println("Invalid username or password.");
        }
        return null;
    }

    public static boolean createAccount() {
        System.out.println("\n--- Create Account ---");
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        if (username.contains(",")) {
            System.out.println("Username cannot contain commas.");
            return false;
        }

        if (UserRepository.userExists(username)) {
            System.out.println("Username already exists. Please choose another one.");
            return false;
        }

        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return false;
        }

        if (password.contains(",")) {
            System.out.println("Password cannot contain commas.");
            return false;
        }

        User newUser = new User(username);
        return UserRepository.saveUserData(newUser, password);
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