package types;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import static utils.CryptoUtils.decrypt;

public class EncryptedDocInfo_t extends Type_t implements Serializable{

    final private byte[] encryptedDocID;
    final private byte[] encryptedOwner;
    private byte[] encryptedKey;

    public EncryptedDocInfo_t(byte[] docID, byte[] owner, byte[] key) {
        this.encryptedDocID = docID;
        this.encryptedOwner = owner;
        this.encryptedKey = key;
    }

    @Override
    public void print() {
        System.out.println("----DocInfo----");
        System.out.println("Doc ID: " + encryptedDocID.toString());
        System.out.println("Owner:  " + encryptedOwner.toString());
    }


    public String getDocID(PrivateKey key) {
        try {
            return (String) decrypt(key, encryptedDocID);
        } catch ( NoSuchAlgorithmException | IOException | ClassNotFoundException | BadPaddingException
                | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getOwner(PrivateKey key) {
        try {
            return (String) decrypt(key, encryptedOwner);
        } catch ( NoSuchAlgorithmException | IOException | ClassNotFoundException | BadPaddingException
                | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SecretKey getKey(PrivateKey key) {
        try {
            return (SecretKey) decrypt(key, encryptedKey);
        } catch ( NoSuchAlgorithmException | IOException | ClassNotFoundException | BadPaddingException
                | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }



}
