package bankingSystem;

import java.io.*;
import java.util.Scanner;

public class SignUp {
    private String username;
    private String password;
    private String role;

    public SignUp(String username, String password) {
        this.username = username;
        this.password = password;
        role = "Customer";
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.println();
        try {
        File file = new File("accounts.txt");

        if (file.exists()) {
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                String existingUsername = line.split(",")[0];

                if (existingUsername.equals(username)) {
                    System.out.println("Username already exists.");
                    fileScanner.close();
                    return;
                }
            }

            fileScanner.close();
        }


            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.println();
            System.out.print("confirmed Password: ");
            String confirmedPassword = scanner.nextLine();

            if (password.equals(confirmedPassword)) {
                SignUp account = new SignUp(username, password);

                FileWriter writer = new FileWriter("accounts.txt", true);

                writer.write(account.username + "," + account.password + "," + account.role);
                writer.write("\n");

                writer.close();

                System.out.println("Your account has been created!");

            } else{
            System.out.println("Passwords do not match");
        }

    } catch (IOException e) {
        System.out.println("Error accessing account file.");
    }

        scanner.close();
    }
}
