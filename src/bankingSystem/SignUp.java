package bankingSystem;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class SignUp {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean signupSuccessful = false;

        boolean hasAccount = false;
        while (true) {
            System.out.print("Do you have an account (yes or no): ");
            String input = scanner.nextLine();
            System.out.println();

            if (input.equalsIgnoreCase("yes")) {
                hasAccount = true;
                break;
            } else if (input.equalsIgnoreCase("no")) {
                break;
            } else {
                System.out.println("please enter yes or no");
                continue;
            }
        }

        if (!hasAccount) {
            System.out.println("------Sign up------");

            String firstName;

            while (true) {
                System.out.print("First Name: ");
                firstName = scanner.nextLine();

                if (!firstName.matches("[A-Za-z]+")) {
                    System.out.println("First name must contain letters only.");
                    continue;
                } else {
                    break;
                }
            }

            String lastName;
            while (true) {
                System.out.print("\nLast Name: ");
                lastName = scanner.nextLine();

                if (!lastName.matches("[A-Za-z]+")) {
                    System.out.println("Last name must contain letters only.");
                    continue;
                } else {
                    break;
                }
            }

            String email;

            while (true) {
                System.out.print("\nEmail: ");
                email = scanner.nextLine();

                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    System.out.println("Invalid email address.");
                    continue;
                }

                if (UserFile.emailExists(email)) {
                    System.out.println("Email already been used." +
                            "\nPlease enter another email");
                    continue;
                }

                break;
            }
            while (true) {
                System.out.print("\nPassword: ");
                String password = scanner.nextLine();

                if (password.length() < 8 ||
                        !password.matches(".*[A-Z].*") ||
                        !password.matches(".*[a-z].*") ||
                        !password.matches(".*[!@#$%^&*()_+\\-=<>?].*") ||
                        !password.matches(".*[0-9].*")) {

                    System.out.println(
                            "Password must be at least 8 characters\n " +
                                    "and contain uppercase, lowercase, a number, and a special character."
                    );
                    continue;
                }
                System.out.print("Confirmed Password: ");
                String confirmedPassword = scanner.nextLine();

                if (password.equals(confirmedPassword)) {
                    try {
                        byte[] salt = PasswordUtil.generateSalt();

                        String hashedPassword = PasswordUtil.hashPassword(password, salt);

                        String saltString =
                                java.util.HexFormat.of().formatHex(salt);


                        Customer customer = new Customer(firstName, lastName, email);

                        UserFile.saveUser(customer, saltString, hashedPassword);

                        signupSuccessful = true;
                        System.out.println("Your account has been created!");
                        break;
                    } catch (Exception e) {
                        System.out.println("Error creating account.");
                    }

                } else {
                    System.out.println("Passwords do not match");
                    continue;
                }
            }
        }

        if (hasAccount || signupSuccessful) {
            Login.main(new String[]{});
        }

    }
}

