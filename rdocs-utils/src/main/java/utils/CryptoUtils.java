package utils;

import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class CryptoUtils {

    // Asymmetric Encryption

    public static KeyPair setKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            return keyGen.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(PublicKey key, Object message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException
    {
        byte[] messageSerialized = serialize(message);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(messageSerialized);
    }

    public static Object decrypt(PrivateKey key, byte[] cipherSerializedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] messageSerialized = cipher.doFinal(cipherSerializedMessage);
        Object message = deserialize(messageSerialized);
        return message;
    }

    // Signatures
    public static byte[] sign(byte[] unsignedData, PrivateKey prvKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(prvKey);
        sig.update(unsignedData);
        return sig.sign();
    }

    public static boolean verify(byte[] signedData, PublicKey pubKey, byte[] signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(pubKey);
        sig.update(signedData);
        return sig.verify(signature);
    }


    // Symmetric Encryption

    public static SecretKey getRandomSecretKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        return secretKey;
    }

    public static byte[] encrypt (SecretKey key, Object message) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] messageSerialized = serialize(message);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(messageSerialized);
    }

    public static Object decrypt (SecretKey key, byte[] cipherSerializedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException
    {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] messageSerialized = cipher.doFinal(cipherSerializedMessage);
        Object message = deserialize(messageSerialized);
        return message;
    }



    // Serialization
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        byte[] outputBytes = out.toByteArray();
        out.close();
        return outputBytes;
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        Object outputObject = is.readObject();
        in.close();
        return outputObject;
    }




}
