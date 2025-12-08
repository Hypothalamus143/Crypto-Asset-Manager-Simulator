public class InvalidInputException extends DialogException {
    private final String fieldName;

    private InvalidInputException(String message, String dialogTitle,
                                  String fieldName, String suggestion) {
        super(message, dialogTitle, suggestion);
        this.fieldName = fieldName;
    }

    // Factory methods - much simpler now
    public static InvalidInputException emptyField(String fieldName, String dialogTitle) {
        return new InvalidInputException(
                fieldName + " cannot be empty",
                dialogTitle,
                fieldName,
                "Please enter a value for " + fieldName
        );
    }

    public static InvalidInputException invalidFormat(String fieldName, String dialogTitle) {
        return new InvalidInputException(
                fieldName + " has invalid format",
                dialogTitle,
                fieldName,
                "Check the format of " + fieldName
        );
    }

    public static InvalidInputException tooShort(String fieldName, int minLength, String dialogTitle) {
        return new InvalidInputException(
                fieldName + " is too short. Minimum: " + minLength + " characters",
                dialogTitle,
                fieldName,
                "Enter at least " + minLength + " characters"
        );
    }

    public String getFieldName() {
        return fieldName;
    }
}