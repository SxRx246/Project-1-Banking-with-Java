package bankingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.Random;

import static bankingSystem.TransactionManager.reachedLimitPerDay;
import static bankingSystem.TransactionManager.transactionsHistory;


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

        MASTERCARD(5_000, 200_000, 100_000, 20_000, 10_000),

        MASTERCARD_TITANIUM(10_000, 200_000, 100_000, 40_000, 20_000),

        MASTERCARD_PLATINUM(20_000, 200_000, 100_000, 80_000, 40_000);

        private final double withdrawLimit;
        private final double ownDepositLimit;
        private final double otherDepositLimit;
        private final double ownTransferLimit;
        private final double otherTransferLimit;

        DebitCardType(
                double withdrawLimit,
                double ownDepositLimit,
                double otherDepositLimit,
                double ownTransferLimit,
                double otherTransferLimit) {

            this.withdrawLimit = withdrawLimit;
            this.ownDepositLimit = ownDepositLimit;
            this.otherDepositLimit = otherDepositLimit;
            this.ownTransferLimit = ownTransferLimit;
            this.otherTransferLimit = otherTransferLimit;
        }

        public double getWithdrawLimit() {
            return withdrawLimit;
        }

        public double getOwnDepositLimit() {
            return ownDepositLimit;
        }

        public double getOtherDepositLimit() {
            return otherDepositLimit;
        }

        public double getOwnTransferLimit() {
            return ownTransferLimit;
        }

        public double getOtherTransferLimit() {
            return otherTransferLimit;
        }
    }

    private DebitCardType debitCardType;

    private int accountNumber;
    private double balance;
    Login loggedInUser;
    private String email;
    private int overdraftCount;


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

            BankAccountFile.updateAccount(bankAccount);

            System.out.println("Successful Withdraw of " + amount + " BD");
            System.out.println("Balance now in account " + bankAccount.getAccountNumber() + " is " + bankAccount.getBalance());

            return true;
        }
    }

    public static boolean depositMoney(double amount, int accountNumber) {
        if (amount <= 0) {
            System.out.println("Please enter a valid deposit amount.");
            return false;
        }

        double newBalance = 0;

        BankAccount bankAccount = BankAccountFile.findAccount(accountNumber);

        double currentBalance = bankAccount.getBalance();

        if (bankAccount == null) {
            System.out.println("Deposit failed. The account number you entered does not exist.");
            return false;
        }

        newBalance = currentBalance + amount;
        bankAccount.setBalance(newBalance);

        if (newBalance >= 0 && bankAccount.getAccountStatus() == AccountStatus.DEACTIVATED) {
            bankAccount.setAccountStatus(AccountStatus.ACTIVE);
            bankAccount.setOverdraftCount(0);
            System.out.println("Account has been reactivated");
        }

        BankAccountFile.updateAccount(bankAccount);

        System.out.println("Successful Deposit of " + amount + " BD");
        System.out.println("Balance now in account " + accountNumber + " is " + newBalance);

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

        BankAccount toBankAccount = BankAccountFile.findAccount(accountNumber);
        if (toBankAccount == null) {
            System.out.println("Transfer failed. The account number you entered does not exist.");
            return false;
        }

        double myCurrentBalance = myBankAccount.getBalance();
        double myBalance;

        if (amount <= 0) {
            System.out.println("please enter valid amount");
            return false;
        }

        if (myCurrentBalance < amount) {
            System.out.println("Transfer failed. You don't have enough balance.");
            System.out.println("You have only " + myCurrentBalance + " in your account");
            return false;
        }

        myBalance = myCurrentBalance - amount;
        myBankAccount.setBalance(myBalance);

        BankAccountFile.updateAccount(myBankAccount);

        double toBankAccountNewBalance = toBankAccount.getBalance();
        toBankAccountNewBalance += amount;
        toBankAccount.setBalance(toBankAccountNewBalance);

        if (toBankAccountNewBalance >= 0 && toBankAccount.getAccountStatus() == AccountStatus.DEACTIVATED) {
            toBankAccount.setAccountStatus(AccountStatus.ACTIVE);
        }

        BankAccountFile.updateAccount(toBankAccount);

        System.out.println("Successful Transfer of " + amount + " BD");
        System.out.println("Balance now in account " + myBankAccount.getAccountNumber() + " is " + myBankAccount.getBalance());
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
            int numberOfAccounts = BankAccountFile.getUserAccountCount(email);

            if (numberOfAccounts >= 2) {
                System.out.println("You already have 2 accounts.");
                System.out.println("You can only have one Checking and one Savings account.");
                break;
            }

            System.out.print("Do you want to add new bank account?(Enter yes or No)");
            addAccount = scanner.nextLine();


            AccountType accountType = null;
            int accountNumber = 0;
            AccountStatus accountStatus = null;
            double balance = 0;
            int overdraft = 0;
            DebitCardType debitCardType = null;

            if (addAccount.equalsIgnoreCase("yes")) {

                while (true) {
                    boolean hasChecking = BankAccountFile.hasAccountType(email, AccountType.CHECKING);
                    boolean hasSavings = BankAccountFile.hasAccountType(email, AccountType.SAVINGS);

                    System.out.println("\nChoose account type:");

                    if (!hasChecking) {
                        System.out.println("C - Checking");
                    }

                    if (!hasSavings) {
                        System.out.println("S - Savings");
                    }

                    System.out.print("Enter your choice (s or c): ");
                    char choice = scanner.nextLine().charAt(0);

                    if ((choice == 'C' || choice == 'c') && !hasChecking) {
                        accountType = AccountType.CHECKING;
                        break;
                    }

                    if ((choice == 'S' || choice == 's') && !hasSavings) {
                        accountType = AccountType.SAVINGS;
                        break;
                    }
                    System.out.println("Please choose one of the available account types.");
                }

                Random random = new Random();
                accountNumber = 100000 + random.nextInt(900000);

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
                    if (!inputDebitCardType.matches("[1-3]")) {
                        System.out.println("Invalid input. Please enter numbers only from 1 to 3.");
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

                BankAccountFile.addingAccountToFile(bankAccount);

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
            } else {
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

                TransactionManager transactionManager = new TransactionManager();

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
                            transactionManager.addTransactionToFile(currentBankAccount,"WITHDRAW", amount);
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
                            transactionManager.addTransactionToFile(currentBankAccount,"DEPOSIT", amount);
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
                            transactionManager.addTransactionToFile(currentBankAccount,"TRANSFER", amount);
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
            System.out.println("Do you want to view all transactions History?");
            input = scanner.nextLine();
//        scanner.nextLine();

            if (input.equalsIgnoreCase("yes")) {
                transactionsHistory(loggedInUser.getEmail());
                break;
            } else if (input.equalsIgnoreCase("No")) {
                break;
            } else {
                System.out.println("please enter yes or no");
            }
        }

        String filter;
        while (true) {
            System.out.print("\nChoose one filter: " +
                    "\n 1. Today's Transactions" +
                    "\n 2. Yesterday's Transactions" +
                    "\n 3. Last Week Transactions" +
                    "\n 4. Last 7 Days Transactions" +
                    "\n 5. Last Month Transactions" +
                    "\n 6. Last 30 Days" +
                    "\n 7. Exit Transactions Filter" +
                    "\n Enter a number (1-7): ");
            filter = scanner.nextLine();
            if (!filter.matches("[1-7]")) {
                System.out.println("Please enter a valid number(1-7)");
            }

            LocalDate today = LocalDate.now();

            if (filter.equals("1")) {
                try {
                    Files.lines(Path.of("transactions.txt"))
                            .filter(line -> {
                                String date = line.split(",")[0];
                                return date.equals(today.toString());
                            })
                            .forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Transaction file not found");
                }
            } else if (filter.equals("2")) {
                try {
                    Files.lines(Path.of("transactions.txt"))
                            .filter(line -> {
                                String date = line.split(",")[0];
                                LocalDate yesterday = today.minusDays(1);
                                return date.equals(yesterday.toString());
                            })
                            .forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Transaction file not found");
                }
            } else if (filter.equals("3")) {
//                        LocalDate startOfThisWeek = today.with(DayOfWeek.SUNDAY);
                LocalDate startOfThisWeek = today.minusDays((today.getDayOfWeek().getValue() % 7));
                LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
                LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

                System.out.println("Start of last week: " + startOfLastWeek);
                System.out.println("End of last week: " + endOfLastWeek);
                try {
                    Files.lines(Path.of("transactions.txt"))
                            .filter(line -> !line.isBlank())
                            .filter(line -> {
                                        LocalDate date = LocalDate.parse(line.split(",")[0]);
                                        return !date.isBefore(startOfLastWeek) &&
                                                !date.isAfter(endOfLastWeek);
                                    }
                            ).forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Transactions file not found");
                }
            } else if (filter.equals("4")) {
                try {
                    LocalDate last7Days = today.minusDays(6);

                    Files.lines(Path.of("transactions.txt"))
                            .filter(line -> !line.isBlank())
                            .filter(line -> {
                                LocalDate date = LocalDate.parse(line.split(",")[0]);
                                return !date.isBefore(last7Days)
                                        && !date.isAfter(today);
                            })
                            .forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Transaction file not found");
                }
            } else if (filter.equals("5")) {
                try {
                    LocalDate startOfLastMonth =
                            today.minusMonths(1).withDayOfMonth(1);

                    LocalDate endOfLastMonth =
                            today.withDayOfMonth(1).minusDays(1);

                    Files.lines(Path.of("transactions.txt"))
                            .filter(line -> !line.isBlank())
                            .filter(line -> {
                                LocalDate date = LocalDate.parse(line.split(",")[0]);
                                return !date.isBefore(startOfLastMonth)
                                        && !date.isAfter(endOfLastMonth);
                            })
                            .forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Transaction file not found");
                }


            } else if (filter.equals("6")) {
                try {
                    LocalDate last30Days = today.minusDays(29);

                    Files.lines(Path.of("transactions.txt"))
                            .filter(line -> !line.isBlank())
                            .filter(line -> {
                                LocalDate date = LocalDate.parse(line.split(",")[0]);
                                return !date.isBefore(last30Days)
                                        && !date.isAfter(today);
                            })
                            .forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Transaction file not found");
                }
            } else if (filter.equals("7")) {
                break;
            } else {
                System.out.println("Invalid number been entered");
                continue;
            }
        }


        System.out.println("\nThank you !!");

    }

}