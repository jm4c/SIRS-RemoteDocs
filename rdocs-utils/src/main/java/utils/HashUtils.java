package utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static utils.CryptoUtils.*;

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

    public static byte[] getMAC(Object data, SecretKey key) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        final byte[] serializedData = serialize(data);
        Mac mac = Mac.getInstance(key.getAlgorithm());
        mac.init(key);
        return mac.doFinal(serializedData);
    }

}
