import javax.swing.*;

public class CreateAccountRequest implements Validatable{
    private String username;
    private String password;
    private String confirmPassword;

    public CreateAccountRequest(String username, String password, String confirmPassword) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void validate() throws AccountRegistrationException, InvalidInputException {
        // Check for null/empty fields
        if (username == null || username.trim().isEmpty()) {
            throw InvalidInputException.emptyField("Username", "Account Creation Failed");
        }

        if (password == null || password.trim().isEmpty()) {
            throw InvalidInputException.emptyField("Password", "Account Creation Failed");
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw InvalidInputException.emptyField("Password Confirmation", "Account Creation Failed");
        }

        // Trim values for consistency
        String trimmedUsername = username.trim();
        String trimmedPassword = password.trim();
        String trimmedConfirmPassword = confirmPassword.trim();

        // Check password match
        if (!trimmedPassword.equals(trimmedConfirmPassword)) {
            throw AccountRegistrationException.passwordMismatch();
        }

        // Username requirements
        if (trimmedUsername.length() < 3) {
            throw AccountRegistrationException.invalidUsernameFormat("Must be at least 3 characters");
        }

        if (trimmedUsername.length() > 20) {
            throw AccountRegistrationException.invalidUsernameFormat("Cannot exceed 20 characters");
        }

        if (!trimmedUsername.matches("^[a-zA-Z0-9_]+$")) {
            throw AccountRegistrationException.invalidUsernameFormat("Can only contain letters, numbers, and underscores");
        }

        // Check for reserved usernames
        if (isReservedUsername(trimmedUsername.toLowerCase())) {
            throw AccountRegistrationException.invalidUsernameFormat("This username is not allowed");
        }

        // Password requirements
        if (trimmedPassword.length() < 8) {
            throw AccountRegistrationException.weakPassword("Must be at least 8 characters");
        }

        if (trimmedPassword.length() > 50) {
            throw AccountRegistrationException.weakPassword("Cannot exceed 50 characters");
        }

        // Check for at least one uppercase letter
        if (!trimmedPassword.matches(".*[A-Z].*")) {
            throw AccountRegistrationException.weakPassword("Must contain at least one uppercase letter");
        }

        // Check for at least one lowercase letter
        if (!trimmedPassword.matches(".*[a-z].*")) {
            throw AccountRegistrationException.weakPassword("Must contain at least one lowercase letter");
        }

        // Check for at least one digit
        if (!trimmedPassword.matches(".*\\d.*")) {
            throw AccountRegistrationException.weakPassword("Must contain at least one number");
        }

        // Check for at least one special character (optional, but common)
        if (!trimmedPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw AccountRegistrationException.weakPassword("Must contain at least one special character (!@#$%^&* etc.)");
        }

        // Check for common weak passwords
        if (isCommonPassword(trimmedPassword)) {
            throw AccountRegistrationException.weakPassword("This password is too common. Choose a stronger one");
        }

        // Check if password contains username
        if (trimmedPassword.toLowerCase().contains(trimmedUsername.toLowerCase())) {
            throw AccountRegistrationException.weakPassword("Password cannot contain your username");
        }

        // Check for sequential characters (e.g., "1234", "abcd")
        if (hasSequentialCharacters(trimmedPassword, 3)) {
            throw AccountRegistrationException.weakPassword("Avoid sequential characters (like 123 or abc)");
        }

        // Check for repeated characters (e.g., "aaaa", "1111")
        if (hasRepeatedCharacters(trimmedPassword, 3)) {
            throw AccountRegistrationException.weakPassword("Avoid repeated characters (like aaaa or 1111)");
        }
    }

    // Helper methods (could be in a separate utility class)
    private boolean isReservedUsername(String username) {
        String[] reservedNames = {
                "admin", "administrator", "root", "system", "support",
                "help", "info", "contact", "test", "guest", "user"
        };

        for (String reserved : reservedNames) {
            if (username.equals(reserved)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCommonPassword(String password) {
        String[] commonPasswords = {
                "password", "123456", "qwerty", "admin", "welcome",
                "password123", "12345678", "123456789", "1234567890",
                "abc123", "monkey", "dragon", "baseball", "football"
        };

        for (String common : commonPasswords) {
            if (password.equalsIgnoreCase(common)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSequentialCharacters(String str, int length) {
        if (str.length() < length) return false;

        for (int i = 0; i <= str.length() - length; i++) {
            String substring = str.substring(i, i + length);

            // Check numeric sequences
            boolean isNumericSequence = true;
            for (int j = 1; j < substring.length(); j++) {
                if (substring.charAt(j) != substring.charAt(j - 1) + 1) {
                    isNumericSequence = false;
                    break;
                }
            }

            // Check reverse numeric sequences
            boolean isReverseNumericSequence = true;
            for (int j = 1; j < substring.length(); j++) {
                if (substring.charAt(j) != substring.charAt(j - 1) - 1) {
                    isReverseNumericSequence = false;
                    break;
                }
            }

            // Check alphabetic sequences (case insensitive)
            String lowerSubstring = substring.toLowerCase();
            boolean isAlphaSequence = true;
            for (int j = 1; j < lowerSubstring.length(); j++) {
                if (lowerSubstring.charAt(j) != lowerSubstring.charAt(j - 1) + 1) {
                    isAlphaSequence = false;
                    break;
                }
            }

            // Check reverse alphabetic sequences
            boolean isReverseAlphaSequence = true;
            for (int j = 1; j < lowerSubstring.length(); j++) {
                if (lowerSubstring.charAt(j) != lowerSubstring.charAt(j - 1) - 1) {
                    isReverseAlphaSequence = false;
                    break;
                }
            }

            if (isNumericSequence || isReverseNumericSequence ||
                    isAlphaSequence || isReverseAlphaSequence) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRepeatedCharacters(String str, int repeats) {
        if (str.length() < repeats) return false;

        for (int i = 0; i <= str.length() - repeats; i++) {
            char firstChar = str.charAt(i);
            boolean allSame = true;

            for (int j = 1; j < repeats; j++) {
                if (str.charAt(i + j) != firstChar) {
                    allSame = false;
                    break;
                }
            }

            if (allSame) {
                return true;
            }
        }
        return false;
    }
}