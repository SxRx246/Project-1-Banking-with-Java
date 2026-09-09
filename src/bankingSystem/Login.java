package bankingSystem;
import java.io.*;
import java.util.Scanner;

public class Login {
    private String email;
    public Login(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("------Login------");
            System.out.print("email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            File file = new File("accounts.txt");

            if (file.exists()) {
                try {
                    Scanner fileScanner = new Scanner(file);

                    boolean loggedIn = false;

                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        String existingEmail = line.split(",")[2];
                        String salt = line.split(",")[3];
                        String hashedPassword = line.split(",")[4];

                        if (existingEmail.equalsIgnoreCase(email)) {
                            byte[] saltBytes =
                                    java.util.HexFormat.of().parseHex(salt);

                            String enteredPasswordHash =
                                    SignUp.hashPassword(password, saltBytes);

                            if (enteredPasswordHash.equals(hashedPassword)) {
                                loggedIn = true;
                            }

                            break;
                        }
                    }
                    fileScanner.close();

                    if (loggedIn) {
                        System.out.println("You are logged in");

                        Login loggedInUser = new Login(email);
                        Session.setLoggedInUser(loggedInUser);

                        loggedInUser.displayBankAccounts();

                        BankAccount.main(new String[]{});

//                        loggedInUser.displayBankAccounts();
                        break;
                    } else {
                        System.out.println("Incorrect email or password");
                        System.out.println("Please try again!!");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Error reading accounts file.");

                }
            } else {
                System.out.println("No accounts have been created yet.");
            }
        }
            scanner.close();

}
public void displayBankAccounts(){
            File file2 = new File("bankAccounts.txt");
            boolean hasBankAccount = false;
            int bankAcounts = 0;
            if (file2.exists()) {
                try {
                    Scanner fileScanner2 = new Scanner(file2);

                    while (fileScanner2.hasNextLine()) {
                        String line = fileScanner2.nextLine();

                        String accountNumber = line.split(",")[0];
                        String existingEmail = line.split(",")[1];
                        String accountType = line.split(",")[2];
                        double balance = Double.parseDouble(line.split(",")[3]);

                        if (existingEmail.equalsIgnoreCase(this.email)) {
                            hasBankAccount = true;
                            bankAcounts ++;
                            System.out.println("\n------Account" + bankAcounts + "------");
                            System.out.println("Account Number: " + accountNumber);
                            System.out.println("Account Type: " + accountType);
                            System.out.println("Balance: " + balance);
                        }
                    }
                    fileScanner2.close();
                    if (!hasBankAccount){
                        System.out.println("You don't have any bank account yet");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Error reading accounts file.");

                }
            }
            else {
                System.out.println("bankAccounts.txt does not exist.");
            }
        }
    }


