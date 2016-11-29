package utils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static byte[] hash(Object msg, byte[] salt) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("MD5");

        if (salt != null) {
            md.update(salt);
        }

        byte[] serializedMsg = CryptoUtils.serialize(msg);

        return md.digest(serializedMsg);
    }

    public static String hashedString(Object msg, byte[] salt) throws IOException, NoSuchAlgorithmException {
        return DatatypeConverter.printHexBinary(hash(msg, salt));
    }

}
