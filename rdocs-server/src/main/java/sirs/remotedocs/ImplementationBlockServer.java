package sirs.remotedocs;

import interfaces.InterfaceBlockServer;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static utils.CryptoUtils.deserialize;
import static utils.CryptoUtils.serialize;
import static utils.FileUtils.*;
import static utils.HashUtils.hashInText;

public class ImplementationBlockServer extends UnicastRemoteObject implements InterfaceBlockServer {

    private static final long serialVersionUID = 1L;
    private HashMap<String, byte[]>  clientsSalt;


    public ImplementationBlockServer() throws RemoteException {

        //TODO testing loading a list of clients from file and saving it when shutting down server
        try {
            //noinspection unchecked
            clientsSalt = (HashMap<String, byte[]>) deserialize((byte[]) getFile("salt"));
            System.out.println("Client's salt found.");
        } catch (IOException e) {
            System.out.println("Client's salt not found. Creating new map.");
            clientsSalt = new HashMap<>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                storeFile(serialize(clientsSalt),"salt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public boolean usernameExists(String username) throws RemoteException {
        System.out.println("Checking if " + username + " exists...");
        return clientsSalt.containsKey(username);
    }

    @Override
    public byte[] getClientSalt(String username) throws RemoteException {
        return clientsSalt.get(username);
    }

    @Override
    public void storeClientBox(String username, byte[] salt, byte[] encryptedClientBox) throws RemoteException {
        clientsSalt.put(username, salt);
        System.out.println("storing client");

        try {
            String s = hashInText(username, salt);
            new File("./clients/").mkdirs();
            storeFile(encryptedClientBox,"./clients/" + s + ".cbx");
            System.out.println("Stored ClientBox for user " + username + " in:./clients/" + s + ".cbx");

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
            new File("./docs/").mkdirs();
            storeFile(encryptedDocument,"./docs/" + docID + ".sdoc");
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
