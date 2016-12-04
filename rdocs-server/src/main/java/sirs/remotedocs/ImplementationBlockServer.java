package sirs.remotedocs;

import interfaces.InterfaceBlockServer;
import types.ClientInfo_t;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static utils.CryptoUtils.deserialize;
import static utils.CryptoUtils.serialize;
import static utils.FileUtils.*;
import static utils.HashUtils.hashInText;

public class ImplementationBlockServer extends UnicastRemoteObject implements InterfaceBlockServer {

    private static final long serialVersionUID = 1L;
    private HashMap<String, ClientInfo_t> clientsInfoMap;


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

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                storeFile(serialize(clientsInfoMap),"./server/info");
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
    public Set<String> getRegisteredUsers(){
        return clientsInfoMap.keySet();
    }

    @Override
    public byte[] getClientSalt(String username) throws RemoteException {
        return clientsInfoMap.get(username).getSalt();
    }

    @Override
    public void setClientPublicKey(String username, PublicKey key) throws RemoteException {
        clientsInfoMap.get(username).setPublicKey(key);
    }

    @Override
    public PublicKey getClientPublicKey(String username) throws RemoteException {
        return clientsInfoMap.get(username).getPublicKey();
    }

    @Override
    public void storeClientBox(String username, byte[] salt, byte[] encryptedClientBox) throws RemoteException {
        clientsInfoMap.put(username, new ClientInfo_t(salt));
        System.out.println("Storing \"" + username +"\"'s box.");

        try {
            String s = hashInText(username, salt);
            new File("./clients/").mkdirs();
            storeFile(encryptedClientBox,"./server/clients/" + s + ".cbx");
            System.out.println("Stored ClientBox for user " + username + " in:./server/clients/" + s + ".cbx");

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] getClientBox(String username) throws RemoteException {
        try {
            String s = hashInText(username, getClientSalt(username));
            return (byte[])getFile("./clients/" + s + ".cbx");

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
            return (byte[]) getFile("./docs/" + docID + ".sdoc");
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
    public String greeting() throws RemoteException {
        return "Hello There!";
    }

}
