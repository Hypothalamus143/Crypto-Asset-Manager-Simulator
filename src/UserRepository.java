import java.io.*;
import java.util.*;

public class UserRepository {
    private static final String DATA_DIR = "data/users/";
    private static final String AUTH_FILE = "data/auth.csv";

    // Static initializer to set up directories
    static {
        new File(DATA_DIR).mkdirs();
        try {
            new File(AUTH_FILE).createNewFile();
        } catch (IOException e) {
            System.err.println("Error creating auth file: " + e.getMessage());
        }
    }

    // Private constructor to prevent instantiation
    private UserRepository() {}

    public static boolean saveUser(User user, String password) {
        // Save credentials to auth.csv
        if (!saveUserToAuthFile(user.getUsername(), password)) {
            return false;
        }

        // Save user data to individual CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter(getUserFilePath(user.getUsername())))) {
            writer.println(user.getUsername() + "," + user.getBalance());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            return false;
        }
    }

    private static boolean saveUserToAuthFile(String username, String password) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(AUTH_FILE, true))) {
            writer.println(username + "," + password);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user to auth file: " + e.getMessage());
            return false;
        }
    }

    public static User loadUser(String username) {
        // Load user data from individual file
        File userFile = new File(getUserFilePath(username));
        if (!userFile.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    User user = new User(parts[0]);
                    user.setBalance(Double.parseDouble(parts[1]));
                    return user;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading user: " + e.getMessage());
        }
        return null;
    }

    public static boolean userExists(String username) {
        return userExistsInAuthFile(username);
    }

    private static boolean userExistsInAuthFile(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(AUTH_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading auth file: " + e.getMessage());
        }
        return false;
    }

    public static boolean validateCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(AUTH_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading auth file: " + e.getMessage());
        }
        return false;
    }

    private static String getUserFilePath(String username) {
        return DATA_DIR + username + ".csv";
    }
}