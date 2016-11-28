package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;
import types.*;

public interface InterfaceBlockServer extends Remote {

    int BLOCK_MAX_SIZE = 4*1024; //4kB

    //Temporary greeting method for testing
    String greeting() throws RemoteException;
    
    List getPKeyList() throws RemoteException;

    Data_t get(Id_t id) throws RemoteException;

    Id_t put_k(Data_t data, byte[] signature, PublicKey public_key) throws Exception;

    Id_t put_h(Data_t data) throws RemoteException;

    byte[] usernameExists(String username) throws RemoteException;

    void storeClientBox(String username, byte[] salt, byte[] encryptedClientBox) throws RemoteException;

    ClientBox_t getClientBox(String username) throws RemoteException;

}
