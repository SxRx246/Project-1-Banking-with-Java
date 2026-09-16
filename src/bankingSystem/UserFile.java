package bankingSystem;

import javax.imageio.IIOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class UserFile {
    public static void saveUser(Customer customer, String salt, String hashedPassword) {
        try {
            FileWriter writer = new FileWriter("accounts.txt", true);

            writer.write(
                    customer.getFirstName() + "," +
                            customer.getLastName() + "," +
                            customer.getEmail() + "," +
                            salt + "," +
                            hashedPassword + "," +
                            "Customer," +
                            "0," +
                            ""
            );

            writer.write("\n");


            writer.close();
        } catch (IOException e) {
            System.out.println("Error Saving User");
        }
    }

    public static boolean emailExists(String email) {
        try {
            File file = new File("accounts.txt");

            if (file.exists()) {
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    String existingEmail = line.split(",")[2];

                    if (existingEmail.equalsIgnoreCase(email)) {
                        fileScanner.close();
                        return true;
                    }
                }
                fileScanner.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error accessing account file.");
        }
        return false;

    }
}
