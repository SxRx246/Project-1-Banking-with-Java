package bankingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TransactionManager {

    public void addTransactionToFile(BankAccount bankAccount, String type, double amount) {
        try {
            File file = new File("transactions.txt");
            FileWriter writer = new FileWriter(file, true);

            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            writer.write(date + "," +
                    time.format(formatter) + "," +
                    bankAccount.getUserEmail() + "," +
                    bankAccount.getAccountNumber() + "," +
                    bankAccount.getAccountType() + "," +
                    type + "," +
                    amount + "," +
                    bankAccount.getBalance() + "," +
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

    public static boolean reachedLimitPerDay(int accountNumber, BankAccount bankAccount, String
            transactionType, double currentTransactionAmount, int targetAccountNumber) {

        double totalAmountWithdrawPerDay = 0;
        double totalAmountTransferPerDay = 0;
        double totalAmountOwnTransferPerDay = 0;
        double totalAmountDepositPerDay = 0;
        double totalAmountOwnDepositPerDay = 0;

        BankAccount.DebitCardType debitCardType = null;
        boolean ownAccount = false;
        boolean transferToOwnAccount = false;

        File file1 = new File("bankAccounts.txt");

//        to check whether transaction been done to his own account or no, and to get the debitCardType
        try {
            if (file1.exists()) {
                Scanner fileScanner = new Scanner(file1);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",");

                    if (fields.length < 7) {
                        continue;
                    }

                    int existingAccountNumber = Integer.valueOf(fields[0]);
                    String existingEmail = String.valueOf(fields[1]);
                    BankAccount.DebitCardType existingDebitCardType = BankAccount.DebitCardType.valueOf(fields[6]);


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
                fileScanner.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("bankAccounts file not found");
        }

        File file = new File("transactions.txt");

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

                    if (existingAccountNumber == accountNumber && date.equals(String.valueOf(LocalDate.now()))) {
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
                    }
                }
                fileScanner.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Transactions file not found");
        }

        if (debitCardType == null) {
            System.out.println("Account could not be found.");
            return true;
        }

        double limit;
        double totalAmount;

        if (transactionType.equalsIgnoreCase("WITHDRAW")) {

            limit = debitCardType.getWithdrawLimit();
            totalAmount = totalAmountWithdrawPerDay;

        } else if (transactionType.equalsIgnoreCase("DEPOSIT")) {

            if (ownAccount) {
                limit = debitCardType.getOwnDepositLimit();
                totalAmount = totalAmountOwnDepositPerDay;
            } else {
                limit = debitCardType.getOtherDepositLimit();
                totalAmount = totalAmountDepositPerDay;
            }

        } else if (transactionType.equalsIgnoreCase("TRANSFER")) {

            if (transferToOwnAccount) {
                limit = debitCardType.getOwnTransferLimit();
                totalAmount = totalAmountOwnTransferPerDay;
            } else {
                limit = debitCardType.getOtherTransferLimit();
                totalAmount = totalAmountTransferPerDay;
            }

        } else {

            System.out.println("Invalid transaction type.");
            return true;
        }

        if (totalAmount + currentTransactionAmount > limit) {

            System.out.println(
                    "You can't " + transactionType.toLowerCase() + " more than " + limit + " BD per day."
            );

            return true;
        }

        return false;
    }

}
