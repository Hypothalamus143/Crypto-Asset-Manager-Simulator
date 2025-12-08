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

    public User login(LoginAttempt loginAttempt) throws DialogException{
        loginAttempt.validate();
        String username = loginAttempt.getUsername();
        String password = loginAttempt.getPassword();
        // Validate credentials
        try {
            if (userRepository.validateCredentials(username, password)) {
                User user = userRepository.loadUser(username);
                System.out.println("Login successful for: " + username);
                return user;
            }
        } catch (Exception e) {
            throw new DialogException(e.getMessage(), "Login Failed");
        }
        return null;
    }
    public void createAccount(CreateAccountRequest request) throws Exception{
        request.validate();
        String username = request.getUsername();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        // Check if user already exists
        if (userRepository.userExists(username))
            throw AccountRegistrationException.duplicateUsername(username);
        userRepository.saveUserData(new User(username),password);
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