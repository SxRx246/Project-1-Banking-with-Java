package bankingSystem;

public class Banker extends User{
    private String firstName;
    private String lastName;
    private String email;

    public Banker(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    @Override
    public void displayMenu() {

    }
}
