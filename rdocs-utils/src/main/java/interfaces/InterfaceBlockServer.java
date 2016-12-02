package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceBlockServer extends Remote {

    //Temporary greeting method for testing
    String greeting() throws RemoteException;

    boolean usernameExists(String username) throws RemoteException;

    byte[] getClientSalt(String username) throws RemoteException;

    void storeClientBox(String username, byte[] salt, byte[] encryptedClientBox) throws RemoteException;

    byte[] getClientBox(String username) throws RemoteException;

    void storeDocument(String docID, byte[] encryptedDocument) throws RemoteException;

    byte[] getDocument(String docID) throws RemoteException;

    boolean removeDocument(String docID) throws RemoteException;

}
