package bankingSystem;

public class Person {
    private String firstName;
    private String lastName;
    private enum role {
        banker,
        customer
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void changeRole(){

    }
}
