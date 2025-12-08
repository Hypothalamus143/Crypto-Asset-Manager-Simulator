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

    public static AccountRegistrationException duplicateEmail(String email) {
        return new AccountRegistrationException(
                "Email '" + email + "' is already registered",
                "Registration Failed",
                "Use a different email or try to recover your account"
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

    public static AccountRegistrationException invalidUsernameFormat() {
        return new AccountRegistrationException(
                "Username has invalid format",
                "Invalid Username",
                "Use 3-20 characters: letters, numbers, or underscores"
        );
    }

    public static AccountRegistrationException invalidEmailFormat() {
        return new AccountRegistrationException(
                "Email has invalid format",
                "Invalid Email",
                "Enter a valid email address (e.g., user@example.com)"
        );
    }

    public static AccountRegistrationException ageRestriction(int minAge) {
        return new AccountRegistrationException(
                "You must be at least " + minAge + " years old to register",
                "Age Restriction",
                "Registration is only available for users " + minAge + " years and older"
        );
    }

    public static AccountRegistrationException termsNotAccepted() {
        return new AccountRegistrationException(
                "You must accept the Terms of Service",
                "Terms Required",
                "Please read and accept the Terms of Service to continue"
        );
    }

    // Helper method to generate username suggestions
    private static String generateSuffix() {
        // Generate a random number suffix (100-999)
        int randomSuffix = 100 + (int)(Math.random() * 900);
        return String.valueOf(randomSuffix);
    }
}