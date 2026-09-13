package bankingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class BankAccount {
    public enum AccountType {
        CHECKING,
        SAVINGS
    }

    public enum AccountStatus {
        DEACTIVATED,
        ACTIVE
    }

    private AccountType accountType;
    private int accountNumber;
    private double balance;
    Login loggedInUser;
    private String email;
    private AccountStatus accountStatus;
    private int overdraftCount;

    Scanner scanner = new Scanner(System.in);

    public BankAccount(String email, int accountNumber, double balance, AccountType accountType, AccountStatus accountStatus, int overdraftCount) {
        this.email = email;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.accountStatus = accountStatus;
        this.overdraftCount = overdraftCount;


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

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public int getOverdraftCount() {
        return overdraftCount;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }


    public void setOverdraftCount(int overdraftCount) {
        this.overdraftCount = overdraftCount;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static void addingAccountTofile(BankAccount bankAccount) {
        try {
            FileWriter writer = new FileWriter("bankAccounts.txt", true);
            writer.write(
                    bankAccount.getAccountNumber() + "," +
                            bankAccount.getUserEmail() + "," +
                            bankAccount.getBalance() + "," +
                            bankAccount.getAccountType() + "," +
                            bankAccount.getAccountStatus() + "," +
                            bankAccount.getOverdraftCount()
            );

            writer.write("\n");
            writer.close();
            System.out.println("The account has been created");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean withdrawMoney(double amount, BankAccount bankAccount) {
        if (bankAccount.getAccountStatus() == AccountStatus.DEACTIVATED) {
            System.out.println("Withdrawal failed. Your account is deactivated.");
            return false;
        }
        double currentBalance = bankAccount.getBalance();
        double newBalance;
        int overdraftCount = bankAccount.getOverdraftCount();

        newBalance = currentBalance - amount;

        if (amount <= 0) {
            System.out.println("please enter valid amount (more than 0)");
            return false;
        } else if (currentBalance < 0 && amount > 100) {
            System.out.println(
                    "Withdrawal failed. Your account balance is already negative ("
                            + currentBalance + "BD), and you cannot withdraw more than 100 BD while your account is overdrawn."
            );
            return false;
        } else {

//            System.out.println("You have only " + currentBalance + " in your account, your account will be overdraft ");
//        }
//        else if (currentBalance >= amount) {


            if (newBalance < 0) {
                overdraftCount++;
                newBalance -= 35;
                System.out.println("Overdraft occurred." +
                        "\n Overdraft protection fee: 35 BD" +
                        "\n New balance: " + newBalance + " BD");
            }
            if (overdraftCount >= 2) {
                bankAccount.setAccountStatus(AccountStatus.DEACTIVATED);
            }

            bankAccount.setBalance(newBalance);
            bankAccount.setOverdraftCount(overdraftCount);

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
                            fields[4] = String.valueOf(bankAccount.getAccountStatus());
                            fields[5] = String.valueOf(bankAccount.getOverdraftCount());
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

                    System.out.println("Successful Withdraw of " + amount + " BD");
                    System.out.println("Balance now in account " + bankAccount.getAccountNumber() + " is " + bankAccount.getBalance());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return true;
        }
    }

    public static boolean depositMoney(double amount, BankAccount bankAccount) {
        if (amount <= 0) {
            System.out.println("Please enter a valid deposit amount.");
            return false;
        }

        double currentBalance = bankAccount.getBalance();
        double newBalance;

        newBalance = currentBalance + amount;
        bankAccount.setBalance(newBalance);

        if (newBalance >= 0 && bankAccount.getAccountStatus() == AccountStatus.DEACTIVATED) {
            bankAccount.setAccountStatus(AccountStatus.ACTIVE);
            bankAccount.setOverdraftCount(0);
            System.out.println("Account has been reactivated");
        }

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
                        fields[4] = String.valueOf(bankAccount.getAccountStatus());
                        fields[5] = String.valueOf(bankAccount.getOverdraftCount());
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

                System.out.println("Successful Deposit of " + amount + " BD");
                System.out.println("Balance now in account " + bankAccount.getAccountNumber() + " is " + bankAccount.getBalance());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean transferMoney(BankAccount myBankAccount, int accountNumber, double amount) {
        if (myBankAccount.getAccountStatus() == AccountStatus.DEACTIVATED) {
            System.out.println("Transfer failed. Your account is deactivated.");
            return false;
        }

        if (myBankAccount.getAccountNumber() == accountNumber) {
            System.out.println("you can't transfer to the same account");
            return false;
        }

        double myCurrentBalance = myBankAccount.getBalance();
        double myBalance;

        if (amount <= 0) {
            System.out.println("please enter valid amount");
            return false;
        } else if (myCurrentBalance < amount) {
            System.out.println("Transfer failed. You don't have enough balance.");
            System.out.println("You have only " + myCurrentBalance + " in your account");
            return false;
        } else if (myCurrentBalance >= amount) {
            myBalance = myCurrentBalance - amount;
            myBankAccount.setBalance(myBalance);

            File file = new File("bankAccounts.txt");

            if (file.exists()) {
                try {
                    Scanner fileScanner = new Scanner(file);
                    ArrayList<String> lines = new ArrayList<>();

                    boolean isAcountNumberExist = false;
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        String[] fields = line.split(",");
                        if (fields[0].equals(String.valueOf(accountNumber))) {
                            isAcountNumberExist = true;
                        }
                    }

                    if (isAcountNumberExist) {
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

                        System.out.println("Successful Transfer of " + amount + " BD");
                        System.out.println("Balance now in account " + myBankAccount.getAccountNumber() + " is " + myBankAccount.getBalance());
                    } else {
                        System.out.println("Transfer failed. The account number you entered does not exist.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public void addTransactionToFile(String type, double amount) {
        try {
            File file = new File("transactions.txt");
            FileWriter writer = new FileWriter(file, true);
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            writer.write(date + "," +
                    time.format(formatter) + "," +
                    this.getUserEmail() + "," +
                    this.getAccountNumber() + "," +
                    type + "," +
                    amount + "," +
                    this.getBalance() + "," +
                    "\n");

            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing transaction: " + e.getMessage());
        }
    }


    public static void transactionHistory(String email){
        File file = new File("transactions.txt");
        int count = 0;
        try{
            if(file.exists()) {
                Scanner fileScanner = new Scanner(file);
                System.out.printf(
                        "%-5s %-12s %-10s %-16s %-12s %12s %20s%n",
                        "#",
                        "Date",
                        "Time",
                        "Account Number",
                        "Type",
                        "Amount",
                        "Balance"
                );
                for(int i=0; i<=10; i++ ){
                    System.out.print("---------");
                }
                System.out.println();
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    String date = line.split(",")[0];
                    String time = line.split(",")[1];
                    String existingEmail = line.split(",")[2];
                    String accountNumber = line.split(",")[3];
                    String type = line.split(",")[4];
                    double amount = Double.parseDouble(line.split(",")[5]);
                    double balance = Double.parseDouble(line.split(",")[6]);

                    if(existingEmail.equalsIgnoreCase(email)){
                        count++;

                        System.out.printf(
                                "%-5d %-12s %-10s %-16s %-12s %12.2f %20.2f%n",
                                count,
                                date,
                                time,
                                accountNumber,
                                type,
                                amount,
                                balance
                        );
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("Error");
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
            scanner.nextLine();

            AccountType accountType;
            if (checkAccountType == 'c' || checkAccountType == 'C') {
                accountType = AccountType.CHECKING;
            } else if (checkAccountType == 's' || checkAccountType == 'S') {
                accountType = AccountType.SAVINGS;
            } else {
                System.out.println("please enter a valid account type whether 'C' for Checking or 'S' for Saving");
                return;
            }

            AccountStatus accountStatus = AccountStatus.ACTIVE;

            System.out.println("Enter the initial deposit amount: ");
//            double balance = scanner.nextDouble();
//            scanner.nextLine();
            String input = scanner.nextLine();

            if (!input.matches("\\d+(\\.\\d+)?")) {
                System.out.println("Invalid amount. Please enter a valid number.");
                return;
            }

            double balance = Double.parseDouble(input);

            if (balance <= 0) {
                System.out.println("Initial deposit must be greater than 0.");
                return;
            }

            int overdraft = 0;

            BankAccount bankAccount = new BankAccount(email, accountNumber, balance, accountType, accountStatus, overdraft);

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
            String input;

            File file = new File("bankAccounts.txt");

            if (file.exists()) {

                if (numberOfAccounts > 1) {
                    System.out.println("You have multiple bank accounts.");
                    System.out.print("Please enter the account number you want to use for the transaction: ");
                    input = scanner.nextLine();

                    if (!input.matches("\\d+")) {
                        System.out.println("Invalid account number. Please enter numbers only.");
                        continue;
                    }

                    accountNumber = Integer.parseInt(input);
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
                        AccountStatus accountStatus = AccountStatus.valueOf(line.split(",")[4]);
                        int overdraftCount = Integer.parseInt(line.split(",")[5]);
                        if (numberOfAccounts == 1) {
                            if (existingEmail.equalsIgnoreCase(email)) {
                                currentBankAccount = new BankAccount(existingEmail, existingAccountNumber, balance, accountType, accountStatus, overdraftCount);

                                break;
                            }
                        } else if (numberOfAccounts > 1) {
                            if (existingEmail.equalsIgnoreCase(email) && existingAccountNumber == accountNumber) {
                                currentBankAccount = new BankAccount(existingEmail, accountNumber, balance, accountType, accountStatus, overdraftCount);

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
                String inputAmount;
                if (transaction == 'w' || transaction == 'W') {
                    System.out.print("how much money you want to withdraw?");
//                    amount = scanner.nextDouble();
//                    scanner.nextLine();
                    inputAmount = scanner.nextLine();
                    if (!inputAmount.matches("\\d+(\\.\\d+)?")) {
                        System.out.println("Invalid amount. Please enter a valid number.");
                        continue;
                    }
                    amount = Double.parseDouble(inputAmount);

                    boolean successful = withdrawMoney(amount, currentBankAccount);
                    if (successful) {
                        currentBankAccount.addTransactionToFile("WITHDRAW", amount);
                    }
                } else if (transaction == 'd' || transaction == 'D') {
                    System.out.print("how much money you want to deposit?");
//                    amount = scanner.nextDouble();
//                    scanner.nextLine();
                    inputAmount = scanner.nextLine();
                    if (!inputAmount.matches("\\d+(\\.\\d+)?")) {
                        System.out.println("Invalid amount. Please enter a valid number.");
                        continue;
                    }
                    amount = Double.parseDouble(inputAmount);

                    boolean successful = depositMoney(amount, currentBankAccount);
                    if (successful) {
                        currentBankAccount.addTransactionToFile("DEPOSIT", amount);
                    }
                } else if (transaction == 't' || transaction == 'T') {
                    System.out.print("how much money you want to transfer?");
//                    amount = scanner.nextDouble();
//                    scanner.nextLine();
                    inputAmount = scanner.nextLine();
                    if (!inputAmount.matches("\\d+(\\.\\d+)?")) {
                        System.out.println("Invalid amount. Please enter a valid number.");
                        continue;
                    }
                    amount = Double.parseDouble(inputAmount);
                    System.out.println("Enter the account number you want to transfer to: ");
//                    int toAccountNumber = scanner.nextInt();
//                    scanner.nextLine();
                    String inputToAccountNumber = scanner.nextLine();
                    if (!inputToAccountNumber.matches("\\d+")) {
                        System.out.println("Invalid account number. Please enter numbers only.");
                        continue;
                    }

                    int toAccountNumber = Integer.parseInt(inputToAccountNumber);

                    boolean successful = transferMoney(currentBankAccount, toAccountNumber, amount);
                    if (successful) {
                        currentBankAccount.addTransactionToFile("TRANSFER", amount);
                    }
                } else {
                    System.out.println("Please enter valid type of transaction(w or d or t), w for Withdraw Money, d for Deposit Money, t for Transfer Money");
                    return;
                }

                System.out.println("Do you want to have another transaction? (yes or no)");
                String check2 = scanner.nextLine();
//                scanner.nextLine();

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
        System.out.println("Do you want to view transactions History?");
        String input = scanner.nextLine();
//        scanner.nextLine();

        if(input.equalsIgnoreCase("yes")){
            transactionHistory(loggedInUser.getEmail());
        }
        else if(!input.equalsIgnoreCase("No")){
            System.out.println("please enter yes or no");
            return;
        }

    }

}
