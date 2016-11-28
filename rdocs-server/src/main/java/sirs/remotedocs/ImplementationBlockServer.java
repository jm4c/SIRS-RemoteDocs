package sirs.remotedocs;

import blocks.PublicKeyBlock;
import exceptions.IDMismatchException;
import interfaces.InterfaceBlockServer;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import exceptions.InvalidSignatureException;
import types.*;
import utils.HashUtils;
import utils.CryptoUtils;

public class ImplementationBlockServer extends UnicastRemoteObject implements InterfaceBlockServer {

    private static final long serialVersionUID = 1L;
    private final List<Id_t> headerFiles;


    public ImplementationBlockServer() throws RemoteException {
        headerFiles = new ArrayList<>();

    }

    private boolean verifyIntegrity(PublicKeyBlock b) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        return CryptoUtils.verify(b.getData().getValue(), b.getPKey(), b.getSig());
    }

    //for header block
    private Id_t calculateBlockID(PublicKey publicKey) throws NoSuchAlgorithmException, IOException {
        byte[] hash = HashUtils.hash(publicKey.toString(), null);
        return new Id_t(hash);
    }

    //for other blocks
    private Id_t calculateBlockID(Data_t data) throws NoSuchAlgorithmException, IOException {
        byte[] hash = HashUtils.hash(data.getValue(), null);

        return new Id_t(hash);
    }

    @Override
    public List getPKeyList() throws RemoteException {
        return headerFiles;
    }
        
    public Data_t get(Id_t id) throws RemoteException {
        PublicKeyBlock b;
        // Main/Header block
        try {
            String s = id.getValue();
            FileInputStream fin;
            fin = new FileInputStream("./files/" + s + ".dat");
            ObjectInputStream ois = new ObjectInputStream(fin);
            Object obj = ois.readObject();
            if (obj instanceof PublicKeyBlock) {
                System.out.println("\nGot header from:./files/" + s + ".dat");
                b = (PublicKeyBlock) obj;
                ois.close();
                if (!verifyIntegrity(b)) {
                    throw new InvalidSignatureException("Invalid signature.");
                } else {
                    System.out.println("Valid signature");
                }
                return b.getData();
            } else {
                System.out.println("Got content from:./files/" + s + ".dat");
                Data_t data = (Data_t) obj;
                ois.close();
                String blockID = calculateBlockID(data).getValue();
                if (s.compareTo(blockID) != 0) {
                    throw new IDMismatchException("Content IDs are not the same.");
                }
                return data;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Id_t put_k(Data_t data, byte[] signature, PublicKey publicKey) throws RemoteException, InvalidSignatureException {

        try {
            if (!CryptoUtils.verify(data.getValue(), publicKey, signature)) {
                throw new InvalidSignatureException("Invalid signature.");
            }
            System.out.println("signature is valid");

            Id_t id = calculateBlockID(publicKey);
            System.out.println(id.getValue());
            PublicKeyBlock b = new PublicKeyBlock(data, signature, publicKey);

            String s = id.getValue();
            new File("./files/").mkdirs();
            FileOutputStream fout = new FileOutputStream("./files/" + s + ".dat");
            System.out.println("Stored header in:./files/" + s + ".dat");

            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(b);
            oos.close();
            if (!headerFiles.contains(id)) {
                headerFiles.add(id);
            }
            return id;
        } catch (InvalidSignatureException ise) {
            ise.printStackTrace();
            throw ise;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Id_t put_h(Data_t data) throws RemoteException {
        try {
            Id_t id = calculateBlockID(data);
            String s = id.getValue();
            new File("./files/").mkdirs();
            FileOutputStream fout = new FileOutputStream("./files/" + s + ".dat");
            System.out.println("Stored content in:./files/" + s + ".dat");

            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
            oos.close();
//            storeBlock(id, s);

            return id;

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    //TODO ----------------SIRS--------------------------------------------
    private final HashMap<String, byte[]>  clientsSalt = new HashMap<>();

    @Override
    public byte[] usernameExists(String username) throws RemoteException {
        if (clientsSalt.containsKey(username))
            return clientsSalt.get(username);
        return null;
    }

    @Override
    public void storeClientBox(String username, byte[] salt, byte[] encryptedClientBox) throws RemoteException {
        clientsSalt.put(username, salt);
        //TODO

    }

    @Override
    public ClientBox_t getClientBox(String username) throws RemoteException {

        return null;
    }

    @Override
    public String greeting() throws RemoteException {
        return "Hello There!";
    }

}
