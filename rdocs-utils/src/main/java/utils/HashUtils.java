package utils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static utils.CryptoUtils.serialize;

public class HashUtils {

    public static byte[] hash(Object msg, byte[] salt) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("SHA1");

        if (salt != null) {
            md.update(salt);
        }

        byte[] serializedMsg = serialize(msg);

        return md.digest(serializedMsg);
    }

    public static String hashInText(Object msg, byte[] salt) throws IOException, NoSuchAlgorithmException {
        return DatatypeConverter.printHexBinary(hash(msg, salt));
    }

}
