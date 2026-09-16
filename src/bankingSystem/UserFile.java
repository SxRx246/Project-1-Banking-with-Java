package bankingSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class UserFile {
    public static boolean emailExists(String email) {
        try {
            File file = new File("accounts.txt");

            if (file.exists()) {
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",", -1);

                    if (fields.length > 2) {
                        String existingEmail = fields[2];

                        if (existingEmail.equalsIgnoreCase(email)) {
                            fileScanner.close();
                            return true;
                        }
                    }
                }
                fileScanner.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error accessing account file.");
        }
        return false;

    }

    public static Optional<String[]> findUser(String email) {

        File file = new File("accounts.txt");

        try {
            if (file.exists()) {
                Scanner fileScanner = new Scanner(file);

                while (fileScanner.hasNextLine()) {

                    String line = fileScanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] fields = line.split(",", -1);

                    String existingEmail = fields[2];

                    if (fields.length > 2 && existingEmail.equalsIgnoreCase(email)) {
                        fileScanner.close();
                        return Optional.of(fields);
                    }
                }

                fileScanner.close();

            }
        } catch (FileNotFoundException e) {
            System.out.println("Error accessing account file.");
        }

        return Optional.empty();
    }

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

}
