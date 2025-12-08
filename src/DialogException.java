public class DialogException extends Exception {
    private final String dialogTitle;
    private final String suggestion;

    public DialogException(String message, String dialogTitle, String suggestion) {
        super(message);
        this.dialogTitle = dialogTitle;
        this.suggestion = suggestion;
    }

    // Simplified: Just message and title, no suggestion
    public DialogException(String message, String dialogTitle) {
        this(message, dialogTitle, null);
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public String getDialogMessage() {
        if (suggestion == null || suggestion.isEmpty()) {
            return getMessage();
        }
        return getMessage() + "\n\n" + suggestion;
    }
}