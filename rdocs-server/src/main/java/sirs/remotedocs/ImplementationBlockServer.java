package sirs.remotedocs;

import interfaces.InterfaceBlockServer;
import types.ClientBin_t;
import types.ClientInfo_t;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static utils.CryptoUtils.*;
import static utils.FileUtils.*;
import static utils.HashUtils.hashInText;

public class ImplementationBlockServer extends UnicastRemoteObject implements InterfaceBlockServer {

    private static final long serialVersionUID = 1L;
    private HashMap<String, ClientInfo_t> clientsInfoMap;
    private HashMap<String, ClientBin_t> clientsBinsMap;


    public ImplementationBlockServer() throws RemoteException {

        try {
            //noinspection unchecked
            clientsInfoMap = (HashMap<String, ClientInfo_t>) deserialize((byte[]) getFile("./server/info"));
            System.out.println("Client's Information found.");
        } catch (IOException e) {
            System.out.println("Client's information map not found. Creating new map.");
            clientsInfoMap = new HashMap<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            //noinspection unchecked
            clientsBinsMap = (HashMap<String, ClientBin_t>) deserialize((byte[]) getFile("./server/bins"));
            System.out.println("Client's Bins found.");
        } catch (IOException e) {
            System.out.println("Client's Bins map not found. Creating new map.");
            clientsBinsMap = new HashMap<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                storeFile(serialize(clientsInfoMap),"./server/info");
                storeFile(serialize(clientsBinsMap),"./server/bins");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public boolean usernameExists(String username) throws RemoteException {
        System.out.println("Checking if " + username + " exists...");
        return clientsInfoMap.containsKey(username);
    }

    @Override
    public byte[] getClientSalt(String username) throws RemoteException {
        return clientsInfoMap.get(username).getSalt();
    }

    @Override
    public Set<String> getRegisteredUsers(){
        return clientsInfoMap.keySet();
    }

    @Override
    public void setUserPublicKey(String username, PublicKey key) throws RemoteException {
        clientsInfoMap.get(username).setPublicKey(key);
    }

    @Override
    public PublicKey getUserPublicKey(String username) throws RemoteException {
        return clientsInfoMap.get(username).getPublicKey();
    }


    @Override
    public void storeClientBox(String username, byte[] salt, byte[] encryptedClientBox) throws RemoteException {
        clientsInfoMap.put(username, new ClientInfo_t(salt));
        System.out.println("Storing \"" + username +"\"'s box.");

        try {
            String s = hashInText(username, salt);
            storeFile(encryptedClientBox,"./server/clients/" + s + ".cbx");
            System.out.println("Stored ClientBox for user " + username + " in:./server/clients/" + s + ".cbx");

            if(!clientsBinsMap.containsKey(username))
                clientsBinsMap.put(username, new ClientBin_t(username));

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] getClientBox(String username) throws RemoteException {
        try {
            String s = hashInText(username, getClientSalt(username));
            return (byte[])getFile("./server/clients/" + s + ".cbx");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void storeDocument(String docID, byte[] encryptedDocument) throws RemoteException {
        try {
            new File("./server/docs/").mkdirs();
            storeFile(encryptedDocument,"./server/docs/" + docID + ".sdoc");
            System.out.println("Stored doc \""+ docID+"\".");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] getDocument(String docID) throws RemoteException {
        try {
            return (byte[]) getFile("./server/docs/" + docID + ".sdoc");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean removeDocument(String docID) throws RemoteException {
        try {
            return deleteFile("./docs/"+docID+".sdoc");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean storeObjectInClientBin(String binOwner, byte[] encryptedDocInfo, String docOwner, byte[] docOwnerSignature) throws RemoteException{
        try {
            if (verify(encryptedDocInfo, getUserPublicKey(docOwner), docOwnerSignature)){
                clientsBinsMap.get(binOwner).addDocument(docOwner, encryptedDocInfo);
                return true;
            }
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public HashMap<String, List<byte[]>> getBinLists(String binOwner) throws RemoteException {
        return clientsBinsMap.get(binOwner).getLists();
        //TODO control downloaded lists but avoid deleting lists not read yet
    }

    @Override
    public String greeting() throws RemoteException {
        return "Hello There!";
    }
}
