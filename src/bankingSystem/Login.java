package bankingSystem;
import java.io.*;
import java.util.Scanner;

public class Login {
    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);


        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.println("Password: ");
        String password = scanner.nextLine();

            File file = new File("accounts.txt");

            if (file.exists()) {
            try {
                Scanner fileScanner = new Scanner(file);

                boolean loggedIn = false;

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    String existingUsername = line.split(",")[0];
                    String salt = line.split(",")[1];
                    String hashedPassword = line.split(",")[2];

                    if (existingUsername.equals(username)) {
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
                } else {
                    System.out.println("Incorrect username or password");
                }
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Error reading accounts file.");

            }
            }else {
                System.out.println("No accounts have been created yet.");
            }
            scanner.close();

}


}
