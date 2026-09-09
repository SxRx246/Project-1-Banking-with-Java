package bankingSystem;

import java.util.HashMap;

public class Customer extends Person{

    //Customers may have a checking account, a savings account, or both.
//    <accountType , accountAmount>
//    HashMap<String,Double> accounts = new HashMap<>();

    public Customer(String firstName, String lastName, String role) {
        super(firstName, lastName, role);
//        this.accounts = accounts;
    }


}
