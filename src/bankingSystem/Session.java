package bankingSystem;

public class Session {

    private static Login loggedInUser;

    public static void setLoggedInUser(Login user) {
        loggedInUser = user;
    }

    public static Login getLoggedInUser() {
        return loggedInUser;
    }
}