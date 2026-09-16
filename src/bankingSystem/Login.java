package bankingSystem;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Login {
    private final String email;

    public Login(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("------Login------");
            System.out.print("email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            File file = new File("accounts.txt");

            boolean loggedIn = false;
            boolean emailFound = false;

            if (file.exists()) {
                try {
                    Scanner fileScanner = new Scanner(file);

                    ArrayList<String> lines = new ArrayList<>();

                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        String[] fields = line.split(",", -1);

                        String existingEmail = fields[2];

                        if (existingEmail.equalsIgnoreCase(email)) {

                            emailFound = true;

                            String salt = fields[3];
                            String hashedPassword = fields[4];

                            int failedAttempts = Integer.parseInt(fields[6]);
                            String lockedUntil = fields[7];

                            if (!lockedUntil.isEmpty() && !lockedUntil.equalsIgnoreCase("null")) {

                                LocalDateTime unlockTime =
                                        LocalDateTime.parse(lockedUntil);

                                if (LocalDateTime.now().isBefore(unlockTime)) {

                                    System.out.println("Account is locked.");
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

                                    System.out.println("Please try again after: "
                                            + unlockTime.format(formatter));

                                    lines.add(line);
                                    continue;
                                }

                                // 1 minute has passed
                                failedAttempts = 0;
                                fields[6] = "0";
                                fields[7] = "";
                            }

                            byte[] saltBytes =
                                    java.util.HexFormat.of().parseHex(salt);

                            String enteredPasswordHash =
                                    PasswordUtil.hashPassword(password, saltBytes);

                            if (enteredPasswordHash.equals(hashedPassword)) {
                                loggedIn = true;

                                fields[6] = "0";
                                fields[7] = "";
                            }
                            else {
                            failedAttempts++;
                            fields[6] = String.valueOf(failedAttempts);

                            System.out.println(
                                    "Incorrect email or password."
                            );

                            if (failedAttempts >= 3) {

                                LocalDateTime unlockTime =
                                        LocalDateTime.now().plusMinutes(1);

                                fields[7] = unlockTime.toString();
                                System.out.println("Too many failed attempts.");
                                System.out.println("Account locked for 1 minute.");

                            } else {
                                System.out.println("Please try again!!");
                            }
                        }
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

                    if (loggedIn) {
                        System.out.println("You are logged in");

                        Login loggedInUser = new Login(email);
                        Session.setLoggedInUser(loggedInUser);

                        loggedInUser.displayBankAccounts();

                        BankAccount.main(new String[]{});

                        break;
                    }

                    if (!emailFound) {
                        System.out.println("Incorrect email or password");
                    }

                } catch (Exception e) {
                    System.out.println("Error during login.");
                }

            } else {
                System.out.println("No accounts have been created yet.");
            }
        }

        scanner.close();
    }

    public int displayBankAccounts() {
        File file2 = new File("bankAccounts.txt");
        boolean hasBankAccount = false;
        int bankAccounts = 0;
        if (file2.exists()) {
            try {
                Scanner fileScanner2 = new Scanner(file2);

                while (fileScanner2.hasNextLine()) {
                    String line = fileScanner2.nextLine();

                    String[] fields = line.split(",");

                    String accountNumber = fields[0];
                    String existingEmail = fields[1];
                    String balance = String.valueOf(fields[2]);
                    String accountType = fields[3];

                    if (existingEmail.equalsIgnoreCase(this.email)) {
                        hasBankAccount = true;
                        bankAccounts++;
                        System.out.println("\n------Account" + bankAccounts + "------");
                        System.out.println("Account Number: " + accountNumber);
                        System.out.println("Account Type: " + accountType);
                        System.out.println("Balance: " + balance);
                    }
                }
                fileScanner2.close();
                if (!hasBankAccount) {
                    System.out.println("You don't have any bank account yet");
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error reading accounts file.");

            }
        } else {
            System.out.println("bankAccounts.txt does not exist.");
        }
        return bankAccounts;
    }
}


