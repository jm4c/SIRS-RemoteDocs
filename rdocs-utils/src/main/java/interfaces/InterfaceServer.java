package interfaces;

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

    boolean storeObjectInClientBin(String binOwner, byte[] encryptedDocInfo, String docOwner, byte[] docOwnerSignature) throws RemoteException;

    HashMap<String, List<byte[]>> getBinLists(String binOwner) throws RemoteException;

}
