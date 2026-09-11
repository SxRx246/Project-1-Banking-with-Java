package bankingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class BankAccount {
    public enum AccountType {
        CHECKING,
        SAVINGS
    }

    private AccountType accountType;
    private int accountNumber;
    private double balance;
    Login loggedInUser;
    private String email;
    private String password;

    Scanner scanner = new Scanner(System.in);

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

    public static void addingAccountTofile(BankAccount bankAccount) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void withdrawMoney(double amount, BankAccount bankAccount) {
        double currentBalance = bankAccount.getBalance();
        double balance;

        if (amount <= 0) {
            System.out.println("please enter valid amount");
        } else if (currentBalance < amount) {
            System.out.println("You have only " + currentBalance + " in your account");
        } else if (currentBalance >= amount) {
            balance = currentBalance - amount;
            bankAccount.setBalance(balance);

            File file = new File("bankAccounts.txt");

            if (file.exists()) {
                try {
                    Scanner fileScanner = new Scanner(file);
                    ArrayList<String> lines = new ArrayList<>();

                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        String[] fields = line.split(",");
                        if (fields[0].equals(String.valueOf(bankAccount.getAccountNumber()))) {
                            fields[2] = String.valueOf(bankAccount.getBalance());
                            line = String.join(",", fields);
                        }
                        lines.add(line);
                    }
                    fileScanner.close();

                    FileWriter writer = new FileWriter(file);

                    for (String line : lines) {
                        writer.write(line + "\n");
                    }

                    writer.close();

                    System.out.println("Successful Withdraw of " + amount + "BD");
                    System.out.println("Balance now in account " + bankAccount.getAccountNumber() + " is " + bankAccount.getBalance());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void depositMoney(double amount, BankAccount bankAccount) {
        double currentBalance = bankAccount.getBalance();
        double balance;

        balance = currentBalance + amount;
        bankAccount.setBalance(balance);

        File file = new File("bankAccounts.txt");

        if (file.exists()) {
            try {
                Scanner fileScanner = new Scanner(file);
                ArrayList<String> lines = new ArrayList<>();

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    String[] fields = line.split(",");
                    if (fields[0].equals(String.valueOf(bankAccount.getAccountNumber()))) {
                        fields[2] = String.valueOf(bankAccount.getBalance());
                        line = String.join(",", fields);
                    }
                    lines.add(line);
                }
                fileScanner.close();

                FileWriter writer = new FileWriter(file);

                for (String line : lines) {
                    writer.write(line + "\n");
                }

                writer.close();

                System.out.println("Successful Deposit of " + amount + "BD");
                System.out.println("Balance now in account " + bankAccount.getAccountNumber() + " is " + bankAccount.getBalance());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void transferMoney(BankAccount myBankAccount, int accountNumber, double amount) {

        if (myBankAccount.getAccountNumber() == accountNumber) {
            System.out.println("you can't transfer to the same account");
            return;
        }

        double myCurrentBalance = myBankAccount.getBalance();
        double myBalance;

        if (amount <= 0) {
            System.out.println("please enter valid amount");
            return;
        } else if (myCurrentBalance < amount) {
            System.out.println("Transfer failed. You don't have enough balance.");
            System.out.println("You have only " + myCurrentBalance + " in your account");
            return;
        } else if (myCurrentBalance >= amount) {
            myBalance = myCurrentBalance - amount;
            myBankAccount.setBalance(myBalance);
//            toBankAccount.setBalance(toBankAccount.getBalance()+amount);

            File file = new File("bankAccounts.txt");

            if (file.exists()) {
                try {
                    Scanner fileScanner = new Scanner(file);
                    ArrayList<String> lines = new ArrayList<>();

                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        String[] fields = line.split(",");
                        if (fields[0].equals(String.valueOf(myBankAccount.getAccountNumber()))) {
                            fields[2] = String.valueOf(myBankAccount.getBalance());
                            line = String.join(",", fields);
                        } else if (fields[0].equals(String.valueOf(accountNumber))) {
                            double ToAccountBalance = Double.parseDouble(fields[2]);
                            ToAccountBalance += amount;
                            fields[2] = String.valueOf(ToAccountBalance);
                            line = String.join(",", fields);
                        }
                        lines.add(line);
                    }
                    fileScanner.close();

                    FileWriter writer = new FileWriter(file);

                    for (String line : lines) {
                        writer.write(line + "\n");
                    }

                    writer.close();

                    System.out.println("Successful Transfer of " + amount + "BD");
                    System.out.println("Balance now in account " + myBankAccount.getAccountNumber() + " is " + myBankAccount.getBalance());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Login loggedInUser = Session.getLoggedInUser();

        if (loggedInUser == null) {
            System.out.println("Please login first before accessing your bank account.");
            return;
        }

        String email = loggedInUser.getEmail();

        System.out.print("Do you want to add new bank account?(Enter yes or No)");
        String addAccount = scanner.nextLine();


        if (addAccount.equalsIgnoreCase("yes")) {
            Random random = new Random();
            int accountNumber = 100000 + random.nextInt(900000);

            System.out.println("Account Type Checking or Saving?(Enter C or S)");
            char checkAccountType = scanner.next().charAt(0);

            AccountType accountType;
            if (checkAccountType == 'c' || checkAccountType == 'C') {
                accountType = AccountType.CHECKING;
            } else if (checkAccountType == 's' || checkAccountType == 'S') {
                accountType = AccountType.SAVINGS;
            } else {
                System.out.println("please enter a valid account type whether 'C' for Checking or 'S' for Saving");
                return;
            }

            System.out.println("Enter the initial deposit amount: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();

            BankAccount bankAccount = new BankAccount(email, accountNumber, balance, accountType);

            addingAccountTofile(bankAccount);
        }

        boolean startTransaction = false;
        System.out.print("Do you want to start a transaction?(yes or no)");
        String check = scanner.nextLine();

        if (check.equalsIgnoreCase("yes")) {
            startTransaction = true;
        } else if (!check.equalsIgnoreCase("No")) {
            System.out.println("Please enter yes or no");
        }


        int numberOfAccounts = loggedInUser.displayBankAccounts();


        while (startTransaction) {
            BankAccount currentBankAccount = null;
            int accountNumber;

            File file = new File("bankAccounts.txt");

            if (file.exists()) {

                if (numberOfAccounts > 1) {
                    System.out.println("You have multiple bank accounts.");
                    System.out.print("Please enter the account number you want to use for the transaction: ");
                    accountNumber = scanner.nextInt();
                    scanner.nextLine();
                } else {
                    accountNumber = 0;
                }
                try {
                    Scanner fileScanner = new Scanner(file);
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        int existingAccountNumber = Integer.parseInt(line.split(",")[0]);
                        String existingEmail = line.split(",")[1];
                        double balance = Double.parseDouble(line.split(",")[2]);
                        AccountType accountType = AccountType.valueOf(line.split(",")[3]);
                        if (numberOfAccounts == 1) {
                            if (existingEmail.equalsIgnoreCase(email)) {
                                currentBankAccount = new BankAccount(existingEmail, existingAccountNumber, balance, accountType);

                                break;
                            }
                        } else if (numberOfAccounts > 1) {
                            if (existingEmail.equalsIgnoreCase(email) && existingAccountNumber == accountNumber) {
                                currentBankAccount = new BankAccount(existingEmail, accountNumber, balance, accountType);

                                break;
                            }
                        }
                    }
                    fileScanner.close();
                } catch (FileNotFoundException e) {
                    System.out.println("Error reading bank account file.");
                }

                if (currentBankAccount == null) {
                    System.out.println("Account could not be found.");
                    continue;
                }

                System.out.println("Withdraw money(w), Deposit Money(d), Transfer Money(t). (Enter w or d or t)");
                char transaction = scanner.next().charAt(0);
                scanner.nextLine();

                double amount = 0;
                if (transaction == 'w' || transaction == 'W') {
                    System.out.print("how much money you want to withdraw?");
                    amount = scanner.nextDouble();
                    scanner.nextLine();
                    withdrawMoney(amount, currentBankAccount);
                } else if (transaction == 'd' || transaction == 'D') {
                    System.out.print("how much money you want to deposit?");
                    amount = scanner.nextDouble();
                    scanner.nextLine();
                    depositMoney(amount, currentBankAccount);
                } else if (transaction == 't' || transaction == 'T') {
                    System.out.print("how much money you want to transfer?");
                    amount = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.println("Enter the account number you want to transfer to: ");
                    int toAccountNumber = scanner.nextInt();
                    scanner.nextLine();
                    transferMoney(currentBankAccount, toAccountNumber, amount);
                } else {
                    System.out.println("Please enter valid type of transaction(w or d or t), w for Withdraw Money, d for Deposit Money, t for Transfer Money");
                    return;
                }

                System.out.println("Do you want to have another transaction in this account? (yes or no");
                String check2 = scanner.nextLine();

                if (check2.equalsIgnoreCase("No")) {
                    startTransaction = false;
                } else if (check2.equalsIgnoreCase("yes")) {
                    continue;
                } else {
                    System.out.println("please enter (yes or no)");
                    return;
                }

            }

        }

    }

}
