public class AccountRegistrationException extends DialogException {

    // PRIVATE constructor - force use of factory methods
    private AccountRegistrationException(String message, String dialogTitle, String suggestion) {
        super(message, dialogTitle, suggestion);
    }

    // FACTORY METHODS - each returns a new AccountRegistrationException instance

    public static AccountRegistrationException duplicateUsername(String username) {
        return new AccountRegistrationException(
                "Username '" + username + "' is already taken",
                "Registration Failed",
                "Try: " + username + generateSuffix() + " or choose a different username"
        );
    }

    public static AccountRegistrationException weakPassword(String requirement) {
        return new AccountRegistrationException(
                "Password does not meet security requirements",
                "Weak Password",
                "Requirement: " + requirement
        );
    }

    public static AccountRegistrationException passwordMismatch() {
        return new AccountRegistrationException(
                "Passwords do not match",
                "Password Error",
                "Make sure both password fields match exactly"
        );
    }

    public static AccountRegistrationException invalidUsernameFormat(String requirement) {
        return new AccountRegistrationException(
                "Username has invalid format",
                "Invalid Username",
                "Requirement: " + requirement
        );
    }
    // Helper method to generate username suggestions
    private static String generateSuffix() {
        // Generate a random number suffix (100-999)
        int randomSuffix = 100 + (int)(Math.random() * 900);
        return String.valueOf(randomSuffix);
    }
}