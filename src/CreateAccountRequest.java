public class CreateAccountRequest {
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

    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                confirmPassword != null && !confirmPassword.trim().isEmpty();
    }
}