package bankingSystem;

import java.io.File;
import java.io.FileNotFoundException;
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
    private String password;

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

    public AccountType getAccountType() {
        return accountType;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static void addingAccountTofile(BankAccount bankAccount){
        File file2 = new File("bankAccounts.txt");
        try {
            Scanner fileScanner = new Scanner(file2);

            FileWriter writer = new FileWriter("bankAccounts.txt", true);
            writer.write(
                    bankAccount.getAccountNumber() + "," +
                            bankAccount.getUserEmail() + "," +
                            bankAccount.getBalance() + "," +
                            bankAccount.getAccountType()
            );

            writer.write("\n");
            writer.close();
            System.out.println("The account has been created");

    }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void withdrawMoney(double amount, BankAccount bankAccount){
        double currentBalance = bankAccount.getBalance();
        double balance = currentBalance - amount;

        if(amount<=0){
            System.out.println("please enter valid amount");
        }
        else if(currentBalance < amount){
            System.out.println("You have only "+ currentBalance + " in your account");
        }
        else if(currentBalance>= amount) {
            bankAccount.setBalance(balance);

            File file = new File("bankAccounts.txt");

            if (file.exists()) {
                Scanner fileScanner = null;
                try {
                    fileScanner = new Scanner(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    String existingEmail = line.split(",")[1];

                    if (existingEmail.equals(bankAccount.email)) {
                        String[] fields = line.split(",");
                        fields[2] = String.valueOf(bankAccount.getBalance());
                        line = String.join(",", fields);
                        System.out.println("Successful Withdraw of "+ amount + "BD");
                        fileScanner.close();
                        break;
                        //
                    }
                }
                fileScanner.close();
            }
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
