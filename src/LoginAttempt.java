public class LoginAttempt implements Validatable{
    private String username;
    private String password;

    public LoginAttempt(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void validate() throws InvalidInputException{
        if(username == null || username.trim().isEmpty())
            throw InvalidInputException.emptyField("Username", "Login Failed");
        else if(password == null || password.trim().isEmpty())
            throw InvalidInputException.emptyField("Password", "Login Failed");
    }
}