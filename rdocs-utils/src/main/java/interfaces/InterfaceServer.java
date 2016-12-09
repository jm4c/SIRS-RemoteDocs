package interfaces;

import types.EncryptedDocInfo_t;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;

public interface InterfaceServer extends Remote {

    //Temporary greeting method for testing
    String greeting() throws RemoteException;

    boolean usernameExists(String username) throws RemoteException;

    byte[] getClientSalt(String username) throws RemoteException;

    String[] getRegisteredUsers() throws RemoteException;

    void setUserPublicKey(String username, PublicKey key) throws  RemoteException;

    PublicKey getUserPublicKey(String username) throws RemoteException;

    void storeClientBox(String username, PublicKey publicKey, byte[] salt, byte[] encryptedClientBox) throws RemoteException;

    byte[] getClientBox(String username) throws RemoteException;

    void storeDocument(String docID, byte[] encryptedDocument) throws RemoteException;

    byte[] getDocument(String docID) throws RemoteException;

    boolean removeDocument(String docID) throws RemoteException;

    void storeObjectInClientBin(String binOwner, EncryptedDocInfo_t encryptedDocInfo, String docOwner) throws RemoteException;

    HashMap<String, List<EncryptedDocInfo_t>> getBinLists(String binOwner) throws RemoteException;

    void emptyBin(String binOwner, byte[] signedSerializedDate, byte[] signature) throws RemoteException;

}
