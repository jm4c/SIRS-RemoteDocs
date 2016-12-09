package sirs.remotedocs;

import interfaces.InterfaceServer;
import types.ClientBin_t;
import types.ClientInfo_t;
import types.EncryptedDocInfo_t;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static utils.CryptoUtils.*;
import static utils.FileUtils.*;
import static utils.HashUtils.hashInText;
import static utils.MiscUtils.getStringArrayFromCollection;

public class ImplementationServer extends UnicastRemoteObject implements InterfaceServer {

    private static final long serialVersionUID = 1L;
    private HashMap<String, ClientInfo_t> clientsInfoMap;
    private HashMap<String, ClientBin_t> clientsBinsMap;


    public ImplementationServer() throws RemoteException {

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
    public String[] getRegisteredUsers(){
        return getStringArrayFromCollection(clientsInfoMap.keySet());
    }


    @Override
    public PublicKey getUserPublicKey(String username) throws RemoteException {

        PublicKey key = clientsInfoMap.get(username).getPublicKey();

        return key;
    }


    @Override
    public void storeClientBox(String username, PublicKey publicKey, byte[] salt, byte[] encryptedClientBox) throws RemoteException {
        clientsInfoMap.put(username, new ClientInfo_t(salt, publicKey));
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
            return deleteFile("./server/docs/"+docID+".sdoc");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void storeObjectInClientBin(String binOwner, EncryptedDocInfo_t encryptedDocInfo, String docOwner) throws RemoteException{
        System.out.println("storing in " + binOwner + "'s bin.");
        clientsBinsMap.get(binOwner).addDocument(docOwner, encryptedDocInfo);
    }

    @Override
    public HashMap<String, List<EncryptedDocInfo_t>> getBinLists(String binOwner) throws RemoteException {
        return clientsBinsMap.get(binOwner).getLists();
    }

    @Override
    public void emptyBin(String binOwner, byte[] signedSerializedDate, byte[] signature) throws RemoteException {
        try {
            // signature guarantees authenticity, timestamp guarantees freshness
            Date minimumDate = new Date(new Date().getTime() - (60*1000L)); // signed_timestamp must be after (current_time - 1 minute)
            if(verify(signedSerializedDate, getUserPublicKey(binOwner), signature) && ((Date) deserialize(signedSerializedDate)).after(minimumDate))
                clientsBinsMap.get(binOwner).emptyBin();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String greeting() throws RemoteException {
        return "Successfully connected to server.";
    }

}
