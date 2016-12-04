package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.Set;

public interface InterfaceBlockServer extends Remote {

    //Temporary greeting method for testing
    String greeting() throws RemoteException;

    boolean usernameExists(String username) throws RemoteException;

    Set<String> getRegisteredUsers() throws RemoteException;

    byte[] getClientSalt(String username) throws RemoteException;

    void setClientPublicKey (String username, PublicKey key) throws  RemoteException;

    PublicKey getClientPublicKey(String username) throws RemoteException;

    void storeClientBox(String username, byte[] salt, byte[] encryptedClientBox) throws RemoteException;

    byte[] getClientBox(String username) throws RemoteException;

    void storeDocument(String docID, byte[] encryptedDocument) throws RemoteException;

    byte[] getDocument(String docID) throws RemoteException;

    boolean removeDocument(String docID) throws RemoteException;

}
