package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import types.*;

public interface InterfaceBlockServer extends Remote {

    int BLOCK_MAX_SIZE = 4*1024; //4kB

    //Temporary greeting method for testing
    String greeting() throws RemoteException;
    
    List getPKeyList() throws RemoteException;

    Data_t get(Id_t id) throws RemoteException;

    Id_t put_k(Data_t data, Sig_t signature, Pk_t public_key) throws Exception;

    Id_t put_h(Data_t data) throws RemoteException;

}
