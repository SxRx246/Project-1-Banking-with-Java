package bankingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class BankAccountFile {
    public static BankAccount findAccount(int accountNumber){

        File file = new File("bankAccounts.txt");

        if (file.exists()) {
            try {
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",");
                    if (fields[0].equals(String.valueOf(accountNumber))) {
                        String email = String.valueOf(fields[1]);
                        double balance = Double.parseDouble(fields[2]);
                        BankAccount.AccountType accountType = BankAccount.AccountType.valueOf(fields[3]);
                        BankAccount.AccountStatus accountStatus = BankAccount.AccountStatus.valueOf(fields[4]);
                        int overdraftCount = Integer.parseInt(fields[5]);
                        BankAccount.DebitCardType debitCardType = BankAccount.DebitCardType.valueOf(fields[6]);

                        BankAccount bankAccount = new BankAccount(email, accountNumber, balance, accountType, accountStatus, overdraftCount, debitCardType);
                        return bankAccount;
                    }
                }
                        fileScanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void updateAccount(BankAccount bankAccount) {
        File file = new File("bankAccounts.txt");

        if (file.exists()) {
            try {
                Scanner fileScanner = new Scanner(file);
                ArrayList<String> lines = new ArrayList<>();

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

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
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
        else {
            System.out.println("bank accounts file not found");
        }
    }
    public static void addingAccountTofile(bankingSystem.BankAccount bankAccount) {
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

    public static int getUserAccountCount(String email) {

        int count = 0;

        File file = new File("bankAccounts.txt");

        if (file.exists()) {
            try {
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",");

                    if (fields[1].equalsIgnoreCase(email)) {
                        count++;
                    }
                }

                fileScanner.close();

            } catch (FileNotFoundException e) {
                System.out.println("Error reading bank accounts.");
            }
        }

        return count;
    }

    public static boolean hasAccountType(String email, bankingSystem.BankAccount.AccountType type) {

        File file = new File("bankAccounts.txt");

        if (file.exists()) {
            try {
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",");

                    if (fields[1].equalsIgnoreCase(email)
                            && bankingSystem.BankAccount.AccountType.valueOf(fields[3]) == type) {
                        fileScanner.close();
                        return true;
                    }
                }

                fileScanner.close();

            } catch (Exception e) {
                System.out.println("Error reading bank accounts.");
            }
        }

        return false;
    }

}
