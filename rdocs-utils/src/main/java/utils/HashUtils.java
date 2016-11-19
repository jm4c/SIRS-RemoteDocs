package utils;

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

}
