package bankingSystem;

public class Customer extends User {

    private String firstName;
    private String lastName;
    private String email;

    public Customer(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    @Override
    public void displayMenu() {

    }
}
