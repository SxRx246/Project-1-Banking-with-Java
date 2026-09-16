package bankingSystem;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class PasswordUtil {

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec =
                new PBEKeySpec(password.toCharArray(), salt, 10000, 256);

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        return java.util.HexFormat.of().formatHex(
                factory.generateSecret(spec).getEncoded()
        );
    }
}
