package bankingSystem;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

import static bankingSystem.PasswordUtil.hashPassword;

public class SignUp {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private int failedAttempts;
    private LocalDateTime lockedUntil;

    public SignUp(String firstName,String lastName,String email, String password,int failedAttempts, LocalDateTime lockedUntil ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        role = "Customer";
        this.failedAttempts = failedAttempts;
        this.lockedUntil = lockedUntil;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean signupSuccessful = false;
        while (true) {
        System.out.println("------Sign up------");
            System.out.print("First Name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")){
                System.out.println("Invalid email address.");
                continue;
            }

            try {
                File file = new File("accounts.txt");

                boolean emailExists = false;

                if (file.exists()) {
                    Scanner fileScanner = new Scanner(file);

                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();

                        String existingEmail = line.split(",")[2];

                        if (existingEmail.equals(email)) {
                            System.out.println("Email already been used.");
                            fileScanner.close();
                            continue;
//                            return;
                        }
                    }

                    fileScanner.close();
                }

                System.out.print("Password: ");
                String password = scanner.nextLine();

                if (password.length() < 8 ||
                        !password.matches(".*[A-Z].*") ||
                        !password.matches(".*[a-z].*") ||
                        !password.matches(".*[!@#$%^&*()_+\\-=<>?].*") ||
                        !password.matches(".*[0-9].*")) {

                    System.out.println(
                            "Password must be at least 8 characters " +
                                    "and contain uppercase, lowercase, a number, and a special character."
                    );
                    continue;
                }
                System.out.print("Confirmed Password: ");
                String confirmedPassword = scanner.nextLine();

                if (password.equals(confirmedPassword)) {
                    byte[] salt = PasswordUtil.generateSalt();

                    String hashedPassword = hashPassword(password, salt);

                    String saltString =
                            java.util.HexFormat.of().formatHex(salt);

                    LocalDateTime lockedUntil = null;

                    SignUp account = new SignUp(firstName,lastName, email, hashedPassword, 0, lockedUntil);

                    FileWriter writer = new FileWriter("accounts.txt", true);

                    writer.write(
                            account.firstName + "," +
                            account.lastName + "," +
                            account.email + "," +
                                    saltString + "," +
                                    hashedPassword + "," +
                                    account.role + "," +
                                    account.failedAttempts + "," +
                                    account.lockedUntil
                    );

                    writer.write("\n");


                    writer.close();

                    signupSuccessful = true;
                    System.out.println("Your account has been created!");

                } else {
                    System.out.println("Passwords do not match");
                    continue;
                }

            } catch (Exception e) {
                System.out.println("Error accessing account file.");
                continue;
            }


        if (signupSuccessful) {
            break;
        }
        }
            scanner.close();
    }
}
