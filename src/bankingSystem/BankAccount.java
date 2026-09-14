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

    private AccountType accountType;

    public enum AccountStatus {
        DEACTIVATED,
        ACTIVE
    }

    private AccountStatus accountStatus;

    public enum DebitCardType {
        MASTERCARD,
        MASTERCARD_TITANIUM,
        MASTERCARD_PLATINUM
    }

    private DebitCardType debitCardType;

    private int accountNumber;
    private double balance;
    Login loggedInUser;
    private String email;
    private int overdraftCount;

    Scanner scanner = new Scanner(System.in);

    public BankAccount(String email, int accountNumber, double balance, AccountType accountType, AccountStatus accountStatus, int overdraftCount, DebitCardType debitCardType) {
        this.email = email;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.accountStatus = accountStatus;
        this.overdraftCount = overdraftCount;
        this.debitCardType = debitCardType;


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

    public DebitCardType getDebitCardType() {
        return debitCardType;
    }

    public void setDebitCardType(DebitCardType debitCardType) {
        this.debitCardType = debitCardType;
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
                            bankAccount.getOverdraftCount() + "," +
                            bankAccount.getDebitCardType()
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

//    public static boolean depositMoney(double amount, BankAccount bankAccount) {
//        if (amount <= 0) {
//            System.out.println("Please enter a valid deposit amount.");
//            return false;
//        }
//
//        double currentBalance = bankAccount.getBalance();
//        double newBalance;
//
//        newBalance = currentBalance + amount;
//        bankAccount.setBalance(newBalance);
//
//        if (newBalance >= 0 && bankAccount.getAccountStatus() == AccountStatus.DEACTIVATED) {
//            bankAccount.setAccountStatus(AccountStatus.ACTIVE);
//            bankAccount.setOverdraftCount(0);
//            System.out.println("Account has been reactivated");
//        }
//
//        File file = new File("bankAccounts.txt");
//
//        if (file.exists()) {
//            try {
//                Scanner fileScanner = new Scanner(file);
//                ArrayList<String> lines = new ArrayList<>();
//
//                while (fileScanner.hasNextLine()) {
//                    String line = fileScanner.nextLine();
//
//                    String[] fields = line.split(",");
//                    if (fields[0].equals(String.valueOf(bankAccount.getAccountNumber()))) {
//                        fields[2] = String.valueOf(bankAccount.getBalance());
//                        fields[4] = String.valueOf(bankAccount.getAccountStatus());
//                        fields[5] = String.valueOf(bankAccount.getOverdraftCount());
//                        line = String.join(",", fields);
//                    }
//                    lines.add(line);
//                }
//                fileScanner.close();
//
//                FileWriter writer = new FileWriter(file);
//
//                for (String line : lines) {
//                    writer.write(line + "\n");
//                }
//
//                writer.close();
//
//                System.out.println("Successful Deposit of " + amount + " BD");
//                System.out.println("Balance now in account " + bankAccount.getAccountNumber() + " is " + bankAccount.getBalance());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return true;
//    }

    public static boolean depositMoney(double amount, int accountNumber) {
        if (amount <= 0) {
            System.out.println("Please enter a valid deposit amount.");
            return false;
        }

        double newBalance = 0;


        File file = new File("bankAccounts.txt");

        if (file.exists()) {
            try {
                Scanner fileScanner = new Scanner(file);
                ArrayList<String> lines = new ArrayList<>();

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    String[] fields = line.split(",");
                    if (fields[0].equals(String.valueOf(accountNumber))) {
                        String email = String.valueOf(fields[1]);
                        double balance = Double.parseDouble(fields[2]);
                        AccountType accountType = AccountType.valueOf(fields[3]);
                        AccountStatus accountStatus = AccountStatus.valueOf(fields[4]);
                        int overdraftCount = Integer.parseInt(fields[5]);
                        DebitCardType debitCardType = DebitCardType.valueOf(fields[6]);
                        BankAccount bankAccount = new BankAccount(email, accountNumber, balance, accountType, accountStatus, overdraftCount, debitCardType);

                        double currentBalance = bankAccount.getBalance();

                        newBalance = currentBalance + amount;
                        bankAccount.setBalance(newBalance);

                        if (newBalance >= 0 && bankAccount.getAccountStatus() == AccountStatus.DEACTIVATED) {
                            bankAccount.setAccountStatus(AccountStatus.ACTIVE);
                            bankAccount.setOverdraftCount(0);
                            System.out.println("Account has been reactivated");
                        }

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
                System.out.println("Balance now in account " + accountNumber + " is " + newBalance);
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

            File file = new File("bankAccounts.txt");

            if (file.exists()) {
                try {
                    Scanner fileScanner = new Scanner(file);
                    ArrayList<String> lines = new ArrayList<>();

                    boolean isAccountNumberExist = false;
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        String[] fields = line.split(",");
                        if (fields[0].equals(String.valueOf(accountNumber))) {
                            isAccountNumberExist = true;
                        }
                    }
                    fileScanner.close();

                    if (!isAccountNumberExist) {
                        System.out.println("Transfer failed. The account number you entered does not exist.");
                        return false;
                    }
                    myBalance = myCurrentBalance - amount;
                    myBankAccount.setBalance(myBalance);

                    fileScanner = new Scanner(file);

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
                    return true;
//                    } else {
//                        System.out.println("Transfer failed. The account number you entered does not exist.");
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
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
                    this.getAccountType() + "," +
                    type + "," +
                    amount + "," +
                    this.getBalance() + "," +
                    "\n");

            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing transaction: " + e.getMessage());
        }
    }


    public static void transactionsHistory(String email) {
        File file = new File("transactions.txt");
        int count = 0;
        try {
            if (file.exists()) {
                Scanner fileScanner = new Scanner(file);
                System.out.printf(
                        "%-5s %-12s %-10s %-16s %-16s %-12s %12s %20s%n",
                        "#",
                        "Date",
                        "Time",
                        "Account Number",
                        "Account Type",
                        "Transaction Type",
                        "Amount",
                        "Balance"
                );
                for (int i = 0; i <= 10; i++) {
                    System.out.print("-----------");
                }
                System.out.println();
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",");

                    if (fields.length < 8) {
                        System.out.println("Skipping invalid transaction record: " + line);
                        continue;
                    }

                    String date = fields[0];
                    String time = fields[1];
                    String existingEmail = fields[2];
                    String accountNumber = fields[3];
                    String accountType = fields[4];
                    String type = fields[5];
                    double amount = Double.parseDouble(fields[6]);
                    double balance = Double.parseDouble(fields[7]);

                    if (existingEmail.equalsIgnoreCase(email)) {
                        count++;

                        System.out.printf(
                                "%-5s %-12s %-10s %-16s %-16s %-12s %12s %20s%n",
                                count,
                                date,
                                time,
                                accountNumber,
                                accountType,
                                type,
                                amount,
                                balance
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean reachedLimitPerDay(int accountNumber, BankAccount bankAccount, String transactionType, double currentTransactionAmount, int targetAccountNumber) {
        File file1 = new File("bankAccounts.txt");
        DebitCardType debitCardType = null;
        boolean ownAccount = false;
        boolean transferToOwnAccount = false;

//        to check whether transaction been done to his own account or no, and to get the debitCardType
        try {
            if (file1.exists()) {
                Scanner fileScanner = new Scanner(file1);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    DebitCardType existingDebitCardType = DebitCardType.valueOf(line.split(",")[6]);
                    int existingAccountNumber = Integer.valueOf(line.split(",")[0]);
                    String existingEmail = String.valueOf(line.split(",")[1]);


                    if (existingAccountNumber == accountNumber) {
                        debitCardType = existingDebitCardType;
                        if (existingEmail.equalsIgnoreCase(bankAccount.getUserEmail())) {
                            ownAccount = true;
                        }
                    } else if (existingAccountNumber == targetAccountNumber) {
                        if (existingEmail.equalsIgnoreCase(bankAccount.getUserEmail())) {
                            transferToOwnAccount = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("error");
        }

        File file = new File("transactions.txt");
        boolean limitReached = false;

        double totalAmountWithdrawPerDay = 0;
        double totalAmountTransferPerDay = 0;
        double totalAmountOwnTransferPerDay = 0;
        double totalAmountDepositPerDay = 0;
        double totalAmountOwnDepositPerDay = 0;

        try {
            if (file.exists()) {
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",");

                    if (fields.length < 8) {
                        System.out.println("Skipping invalid transaction record: " + line);
                        continue;
                    }

                    String date = fields[0];
                    int existingAccountNumber = Integer.parseInt(fields[3]);
                    String existingTransactionType = fields[5];
                    double amount = Double.parseDouble(fields[6]);


//                    String date = line.split(",")[0];
////                    String time = line.split(",")[1];
////                    String existingEmail = line.split(",")[2];
//                    int existingAccountNumber = Integer.valueOf(line.split(",")[3]);
////                    String accountType = line.split(",")[4];
//                    String existingTransactionType = line.split(",")[5];
//                    double amount = Double.parseDouble(line.split(",")[6]);
////                    double balance = Double.parseDouble(line.split(",")[7]);


                    if (existingAccountNumber == accountNumber) {
                        if (date.equals(String.valueOf(LocalDate.now()))) {
                            if (debitCardType == DebitCardType.MASTERCARD_PLATINUM) {
                                if (existingTransactionType.equalsIgnoreCase("WITHDRAW")) {
                                    totalAmountWithdrawPerDay += amount;
                                } else if (existingTransactionType.equalsIgnoreCase("DEPOSIT")) {
                                    if (ownAccount) {
                                        totalAmountOwnDepositPerDay += amount;
                                    } else {
                                        totalAmountDepositPerDay += amount;
                                    }
                                } else if (existingTransactionType.equalsIgnoreCase("TRANSFER")) {
                                    if (transferToOwnAccount) {
                                        totalAmountOwnTransferPerDay += amount;
                                    } else {
                                        totalAmountTransferPerDay += amount;
                                    }
                                }
                            } else if (debitCardType == DebitCardType.MASTERCARD_TITANIUM) {
                                if (existingTransactionType.equalsIgnoreCase("WITHDRAW")) {
                                    totalAmountWithdrawPerDay += amount;
                                } else if (existingTransactionType.equalsIgnoreCase("DEPOSIT")) {
                                    if (ownAccount) {
                                        totalAmountOwnDepositPerDay += amount;
                                    } else {
                                        totalAmountDepositPerDay += amount;
                                    }
                                } else if (existingTransactionType.equalsIgnoreCase("TRANSFER")) {
                                    if (transferToOwnAccount) {
                                        totalAmountOwnTransferPerDay += amount;
                                    } else {
                                        totalAmountTransferPerDay += amount;
                                    }
                                }
                            } else if (debitCardType == DebitCardType.MASTERCARD) {
                                if (existingTransactionType.equalsIgnoreCase("WITHDRAW")) {
                                    totalAmountWithdrawPerDay += amount;
                                } else if (existingTransactionType.equalsIgnoreCase("DEPOSIT")) {
                                    if (ownAccount) {
                                        totalAmountOwnDepositPerDay += amount;
                                    } else {
                                        totalAmountDepositPerDay += amount;
                                    }
                                } else if (existingTransactionType.equalsIgnoreCase("TRANSFER")) {
                                    if (transferToOwnAccount) {
                                        totalAmountOwnTransferPerDay += amount;
                                    } else {
                                        totalAmountTransferPerDay += amount;
                                    }
                                }
                            } else {
                                System.out.println("Invalid Mastercard Type");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (debitCardType == DebitCardType.MASTERCARD_PLATINUM) {
            if (transactionType.equalsIgnoreCase("WITHDRAW")) {
                if (totalAmountWithdrawPerDay + currentTransactionAmount > 20_000) {
                    System.out.println("you can't withdraw more than 20,000 BD per day");
//                            "\n you already withdraw " + totalAmountWithdrawPerDay + " BD today");
                    return true;
                } else {
                    return false;
                }
            } else if (transactionType.equalsIgnoreCase("DEPOSIT")) {
                if (ownAccount) {
                    if (totalAmountOwnDepositPerDay + currentTransactionAmount > 200_000) {
                        System.out.println("you can't deposit more than 200,000 BD per day into your account");
//                                "\n you already deposit " + totalAmountOwnDepositPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (totalAmountDepositPerDay + currentTransactionAmount > 100_000) {
                        System.out.println("you can't deposit more than 100,000 BD per day");
//                                "\n you already deposit " + totalAmountDepositPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                }

            } else if (transactionType.equalsIgnoreCase("TRANSFER")) {
                if (transferToOwnAccount) {
                    if (totalAmountOwnTransferPerDay + currentTransactionAmount > 80_000) {
                        System.out.println("you can't transfer more than 80,000 BD per day from your account");
//                                "\n you already transfer " + totalAmountOwnTransferPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (totalAmountTransferPerDay + currentTransactionAmount > 40_000) {
                        System.out.println("you can't transfer more than 40,000 BD per day into another persons account");
//                                "\n you already transfer " + totalAmountTransferPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                System.out.println("Invalid Transaction Type");
            }
        } else if (debitCardType == DebitCardType.MASTERCARD_TITANIUM) {
            if (transactionType.equalsIgnoreCase("WITHDRAW")) {
                if (totalAmountWithdrawPerDay + currentTransactionAmount > 10_000) {
                    System.out.println("you can't withdraw more than 10,000 BD per day");
//                            "\n you already withdraw " + totalAmountWithdrawPerDay + " BD today");
                    return true;
                } else {
                    return false;
                }
            } else if (transactionType.equalsIgnoreCase("DEPOSIT")) {
                if (ownAccount) {
                    if (totalAmountOwnDepositPerDay + currentTransactionAmount > 200_000) {
                        System.out.println("you can't deposit more than 100,000 BD per day");
//                                "\n you already deposit " + totalAmountOwnDepositPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (totalAmountDepositPerDay + currentTransactionAmount > 100_000) {
                        System.out.println("you can't deposit more than 100,000 BD per day");
//                                "\n you already deposit " + totalAmountDepositPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                }

            } else if (transactionType.equalsIgnoreCase("TRANSFER")) {
                if (transferToOwnAccount) {
                    if (totalAmountOwnTransferPerDay + currentTransactionAmount > 40_000) {
                        System.out.println("you can't transfer more than 40,000 BD per day");
//                                "\n you already transfer " + totalAmountOwnTransferPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (totalAmountTransferPerDay + currentTransactionAmount > 20_000) {
                        System.out.println("you can't transfer more than 40,000 BD per day");
//                                "\n you already transfer " + totalAmountTransferPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                System.out.println("Invalid Transaction Type");
            }
        } else if (debitCardType == DebitCardType.MASTERCARD) {
            if (transactionType.equalsIgnoreCase("WITHDRAW")) {
                if (totalAmountWithdrawPerDay + currentTransactionAmount > 5_000) {
                    System.out.println("you can't withdraw more than 5,000 BD per day");
//                            "\n you already withdraw " + totalAmountWithdrawPerDay + " BD today");
                    return true;
                } else {
                    return false;
                }
            } else if (transactionType.equalsIgnoreCase("DEPOSIT")) {
                if (ownAccount) {
                    if (totalAmountOwnDepositPerDay + currentTransactionAmount > 200_000) {
                        System.out.println("you can't deposit more than 100,000 BD per day");
//                                "\n you already deposit " + totalAmountOwnDepositPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (totalAmountDepositPerDay + currentTransactionAmount > 100_000) {
                        System.out.println("you can't deposit more than 100,000 BD per day");
//                                "\n you already deposit " + totalAmountDepositPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                }

            } else if (transactionType.equalsIgnoreCase("TRANSFER")) {
                if (transferToOwnAccount) {
                    if (totalAmountOwnTransferPerDay + currentTransactionAmount > 20_000) {
                        System.out.println("you can't transfer more than 40,000 BD per day");
//                                "\n you already transfer " + totalAmountOwnTransferPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (totalAmountTransferPerDay + currentTransactionAmount > 10_000) {
                        System.out.println("you can't transfer more than 40,000 BD per day");
//                                "\n you already transfer " + totalAmountTransferPerDay + " BD today");
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            System.out.println("Invalid Transaction Type");
        }
//                            if(date == String.valueOf(LocalDate.now())){
//                                totalAmountPerDay +=amount;
//                            }
//                            if(totalAmountPerDay + currentTransactionAmount>20000){
//                                System.out.println("you can't withdraw more than 20000 BD per day" +
//                                        "\n you already withdraw "+ totalAmountPerDay +" BD today");
//                            }
//                        }
//                        else if()


        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Login loggedInUser = Session.getLoggedInUser();

        if (loggedInUser == null) {
            System.out.println("Please login first before accessing your bank account.");
            return;
        }

        String email = loggedInUser.getEmail();

        String addAccount;
        while (true) {
            System.out.print("Do you want to add new bank account?(Enter yes or No)");
            addAccount = scanner.nextLine();


            AccountType accountType = null;
            int accountNumber = 0;
            AccountStatus accountStatus = null;
            double balance = 0;
            int overdraft = 0;
            DebitCardType debitCardType = null;

            if (addAccount.equalsIgnoreCase("yes")) {
                Random random = new Random();
                accountNumber = 100000 + random.nextInt(900000);

                while (true) {
                    System.out.println("Account Type Checking or Saving?(Enter C or S)");
                    char checkAccountType = scanner.next().charAt(0);
                    scanner.nextLine();

                    if (checkAccountType == 'c' || checkAccountType == 'C') {
                        accountType = AccountType.CHECKING;
                        break;
                    } else if (checkAccountType == 's' || checkAccountType == 'S') {
                        accountType = AccountType.SAVINGS;
                        break;
                    } else {
                        System.out.println("please enter a valid account type whether 'C' for Checking or 'S' for Saving");
                        continue;
                    }
                }

                accountStatus = AccountStatus.ACTIVE;

                while (true) {
                    System.out.println("Enter the initial deposit amount: ");
//            double balance = scanner.nextDouble();
//            scanner.nextLine();
                    String input = scanner.nextLine();

                    if (!input.matches("\\d+(\\.\\d+)?")) {
                        System.out.println("Invalid amount. Please enter a valid number.");
                        continue;
                    }

                    balance = Double.parseDouble(input);

                    if (balance <= 0) {
                        System.out.println("Initial deposit must be greater than 0.");
                        continue;
                    }
                    break;
                }


                while (true) {
                    System.out.println("Choose one type, Enter a number 1, 2 or 3: " +
                            "\n 1. Mastercard Platinum" +
                            "\n 2. Mastercard Titanium" +
                            "\n 3. Mastercard");
                    String inputDebitCardType = scanner.nextLine();
                    if (!inputDebitCardType.matches("\\d+")) {
                        System.out.println("Invalid input. Please enter numbers only.");
                        continue;
                    }
                    int debitCardTypeChoosed = Integer.parseInt(inputDebitCardType);

                    if (debitCardTypeChoosed == 1) {
                        debitCardType = DebitCardType.MASTERCARD_PLATINUM;
                    } else if (debitCardTypeChoosed == 2) {
                        debitCardType = DebitCardType.MASTERCARD_TITANIUM;
                    } else if (debitCardTypeChoosed == 3) {
                        debitCardType = DebitCardType.MASTERCARD;
                    } else {
                        System.out.println("please enter a valid input whether 1 or 2 or 3:");
                        continue;
                    }
                    break;
                }
                BankAccount bankAccount = new BankAccount(email, accountNumber, balance, accountType, accountStatus, overdraft, debitCardType);

                addingAccountTofile(bankAccount);
                break;
            } else if (addAccount.equalsIgnoreCase("no")) {
                break;
            } else {
                System.out.println("please enter yes or no");
            }
        }

        boolean startTransaction = false;
        String check;
        while (true) {
            System.out.print("Do you want to start a transaction?(yes or no)");
            check = scanner.nextLine();

            if (check.equalsIgnoreCase("yes")) {
                startTransaction = true;
                break;
            } else if (check.equalsIgnoreCase("No")) {
                break;
            }
            else {
                System.out.println("Please enter yes or no");
            }
        }


        int numberOfAccounts = loggedInUser.displayBankAccounts();


        while (startTransaction) {
            BankAccount currentBankAccount = null;
            int transactionAccountNumber;
            String input;
            int accountNumber;

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
                    accountNumber = currentBankAccount.getAccountNumber();
                }
                try {
                    Scanner fileScanner = new Scanner(file);
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        int existingAccountNumber = Integer.parseInt(line.split(",")[0]);
                        String existingEmail = line.split(",")[1];
                        double existingBalance = Double.parseDouble(line.split(",")[2]);
                        AccountType existingAccountType = AccountType.valueOf(line.split(",")[3]);
                        AccountStatus existingAccountStatus = AccountStatus.valueOf(line.split(",")[4]);
                        int overdraftCount = Integer.parseInt(line.split(",")[5]);
                        DebitCardType existingDebitCardType = DebitCardType.valueOf(line.split(",")[6]);
                        if (numberOfAccounts == 1) {
                            if (existingEmail.equalsIgnoreCase(email)) {
                                currentBankAccount = new BankAccount(existingEmail, existingAccountNumber, existingBalance, existingAccountType, existingAccountStatus, overdraftCount, existingDebitCardType);

                                break;
                            }
                        } else if (numberOfAccounts > 1) {
                            if (existingEmail.equalsIgnoreCase(email) && existingAccountNumber == accountNumber) {
                                currentBankAccount = new BankAccount(existingEmail, existingAccountNumber, existingBalance, existingAccountType, existingAccountStatus, overdraftCount, existingDebitCardType);

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

                    if (!reachedLimitPerDay(accountNumber, currentBankAccount, "WITHDRAW", amount, 0)) {
                        boolean successful = withdrawMoney(amount, currentBankAccount);
                        if (successful) {
                            currentBankAccount.addTransactionToFile("WITHDRAW", amount);
                        }
                    }

                } else if (transaction == 'd' || transaction == 'D') {
                    System.out.print("Enter account number you want to deposit into:");
//                    amount = scanner.nextDouble();
//                    scanner.nextLine();
                    String inputAccountNumber = scanner.nextLine();
                    if (!inputAccountNumber.matches("\\d+(\\.\\d+)?")) {
                        System.out.println("Invalid account number. Please enter a valid number.");
                        continue;
                    }
                    accountNumber = Integer.parseInt(inputAccountNumber);

                    System.out.print("how much money you want to deposit?");
//                    amount = scanner.nextDouble();
//                    scanner.nextLine();
                    inputAmount = scanner.nextLine();
                    if (!inputAmount.matches("\\d+(\\.\\d+)?")) {
                        System.out.println("Invalid amount. Please enter a valid number.");
                        continue;
                    }
                    amount = Double.parseDouble(inputAmount);
                    if (!reachedLimitPerDay(accountNumber, currentBankAccount, "DEPOSIT", amount, 0)) {
                        boolean successful = depositMoney(amount, accountNumber);
                        if (successful) {
                            currentBankAccount.addTransactionToFile("DEPOSIT", amount);
                        }
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
                    if (!reachedLimitPerDay(accountNumber, currentBankAccount, "TRANSFER", amount, toAccountNumber)) {

                        boolean successful = transferMoney(currentBankAccount, toAccountNumber, amount);
                        if (successful) {
                            currentBankAccount.addTransactionToFile("TRANSFER", amount);
                        }
                    }

                } else {
                    System.out.println("Please enter valid type of transaction(w or d or t), w for Withdraw Money, d for Deposit Money, t for Transfer Money");
                    return;
                }

                while (true) {
                    System.out.println("Do you want to have another transaction? (yes or no)");
                    String check2 = scanner.nextLine();
//                scanner.nextLine();

                    if (check2.equalsIgnoreCase("No")) {
                        startTransaction = false;
                        break;
                    } else if (check2.equalsIgnoreCase("yes")) {
                        break;
                    } else {
                        System.out.println("please enter (yes or no)");
                    }
                }
                if (!startTransaction) {
                    break;
                }
            }

        }
        String input;
        while (true) {
            System.out.println("Do you want to view transactions History?");
            input = scanner.nextLine();
//        scanner.nextLine();

            if (input.equalsIgnoreCase("yes")) {
                transactionsHistory(loggedInUser.getEmail());
                break;
            } else if (input.equalsIgnoreCase("No")) {
                break;
            }
            else {
                System.out.println("please enter yes or no");
            }
        }

        System.out.println("Thank you !!");

    }

}
