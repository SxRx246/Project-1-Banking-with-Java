package bankingSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class BankAccount {
    public enum AccountType {
        CHECKING,
        SAVINGS
    }
    private AccountType accountType;
//    private String userEmail;

    private int accountNumber;
    private double balance;
    Login loggedInUser;
    private String email;

    public BankAccount(String email, int accountNumber, double balance, AccountType accountType) {
        this.email = email;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    public String getUserEmail() {
        return email;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public static void addingAccountTofile(BankAccount bankAccount){
        File file2 = new File("bankAccounts.txt");
        try {
            Scanner fileScanner = new Scanner(file2);

//            while (fileScanner.hasNextLine()) {
//                String line = fileScanner.nextLine();
//
//                String email = line.split(",")[2];
//
//                if(email)

            FileWriter writer = new FileWriter("bankAccounts.txt", true);
            writer.write(
                    bankAccount.getAccountNumber() + "," +
                            bankAccount.getUserEmail() + "," +
                            bankAccount.getAccountNumber() + "," +
                            bankAccount.getBalance()
            );

            writer.write("\n");
            writer.close();
            System.out.println("The account has been created");

    }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to add new bank account?(Enter yes or No)");
        String addAccount = scanner.nextLine();

        if(addAccount.equalsIgnoreCase("yes")) {

            Login loggedInUser = Session.getLoggedInUser();
            String email = loggedInUser.getEmail();


            Random random = new Random();
            int accountNumber = 100000 + random.nextInt(900000);

            System.out.println("Account Type Checking or Saving?(Enter C or S)");
            char checkAccountType = scanner.next().charAt(0);

            AccountType accountType;
            if(checkAccountType== 'c' || checkAccountType=='C'){
                accountType = AccountType.CHECKING;
            }
            else if(checkAccountType=='s' || checkAccountType=='S'){
                accountType = AccountType.SAVINGS;
            }
            else {
                System.out.println("please enter a valid account type whether 'C' for Checking or 'S' for Saving");
//                checkAccountType = scanner.next().charAt(0);
                return;
            }

            System.out.println("Enter the initial deposit amount: ");
            double balance = scanner.nextDouble();

            BankAccount bankAccount = new BankAccount(email, accountNumber, balance, accountType);
            addingAccountTofile(bankAccount);

        }
        else {
            System.out.println("please enter valid input weather 'yes' or 'No': ");
            return;
        }
    }

}
