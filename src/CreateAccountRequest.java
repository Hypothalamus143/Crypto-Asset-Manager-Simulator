import javax.swing.*;

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

    public void isValid() throws AccountRegistrationException, InvalidInputException{
        if(username == null && username.trim().isEmpty())
            throw InvalidInputException.emptyField("Username", "Account Creation Failed");
        else if(password == null && password.trim().isEmpty())
            throw InvalidInputException.emptyField("Password", "Account Creation Failed");
        else if(confirmPassword == null && confirmPassword.trim().isEmpty())
            throw InvalidInputException.emptyField("Password Confirmation", "Account Creation Failed");
        else if (!password.equals(confirmPassword))
            throw AccountRegistrationException.passwordMismatch();
    }
}