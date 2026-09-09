package bankingSystem;
import java.io.*;
import java.util.Scanner;

public class Login {
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


}
