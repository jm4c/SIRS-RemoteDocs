package sirs.remotedocs;

import exceptions.DocumentIntegrityCompromisedException;
import interfaces.InterfaceServer;
import types.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static utils.CryptoUtils.*;
import static utils.HashUtils.hash;
import static utils.HashUtils.hashInText;

public class ImplementationClient {

    private String clientUsername;
    private ClientBox_t clientBox;
    private byte[] clientSalt;
    private SecretKey clientKey;


    private static InterfaceServer server;


    public ImplementationClient() {
        try {
            connectToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username){
        clientUsername = username;
    }

    public String getUsername() {
        return clientUsername;
    }

    public void setClientBox(ClientBox_t clientBox){
        this.clientBox = clientBox;
    }

    public void setClientSalt(byte[] clientSalt) {
        this.clientSalt = clientSalt;
    }

    public SecretKey getClientKey() {
        return clientKey;
    }

    public void setClientKey(SecretKey secretKey) {
        this.clientKey = secretKey;
    }

    public byte[] getClientSalt() {
        return clientSalt;
    }


    public ClientBox_t getClientBox(){
        return clientBox;
    }

    public boolean connectToServer() throws Exception{
        Registry myReg = LocateRegistry.getRegistry("localhost");
        server = (InterfaceServer) myReg.lookup("rdocs.Server");
        System.out.println(server.greeting() + "\n");
        return isConnected();
    }

    public boolean isConnected(){
        return server != null;
    }

    //Account's Operations
    public int register(String username, String password) {
        System.out.println("user: " + username);
        System.out.println("pw:   " + password);

        //TODO fix after debugging, minimum letters and numbers maybe?
        if(username.length()<1/*3*/ || username.length()>21){
            System.out.println("Username must be between 4 and 20 characters long.");
            return 2;
        }

        if(password.length()</*7*/1 || password.length()>65){
            System.out.println("Password must be between 8 and 64 characters long.");
            return 3;
        }

        try {
            if(!server.usernameExists(username)) {
                //new ClientBox
                ClientBox_t clientBox = new ClientBox_t(username);

                byte[] salt = getSalt();

                //Cipher client box with symmetric key obtained
                SecretKey clientSecretKey = getSecretKey(password, salt);

                //using username as initialization vector
                byte[] encryptedBox = encrypt(clientSecretKey,salt,clientBox);

                System.out.println("DATA SENT (encrypted empty box): " + clientBox.toString() + "\n");

                server.storeClientBox(username, clientBox.getPublicKey(), salt, encryptedBox);

                return 0;
            } else{
                System.out.println("Username already exists");
                return 1;
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Server is offline.");
            return -1;
        } catch ( InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IOException
                | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return -2;
        }

    }

    public int login (String username, String password) throws Exception{
        System.out.println("Logging " + username);
        try {
            if(server.usernameExists(username)){
                setClientSalt(server.getClientSalt(username));
                setClientKey(getSecretKey(password, getClientSalt()));

                //downloads encrypted box from server
                byte[] encryptedBox = server.getClientBox(username);
                System.out.println("DATA RECEIVED (encrypted empty box): " + encryptedBox.toString() + "\n");

                //if can't decrypt the box means wrong password
                setClientBox((ClientBox_t) decrypt(getClientKey(), getClientSalt(), encryptedBox));
                setUsername(username);


                return 0;
            }
            System.out.println("Username does not exist.");
            return 2;
        } catch (NullPointerException| RemoteException e){
            e.printStackTrace();
            System.out.println("Server is offline.");
            return -1;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("Wrong password.");
            return 1;
        }
    }

    public void resendClientBox() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] encryptedClientBox = encrypt(getClientKey(), getClientSalt(), getClientBox());
        server.storeClientBox(getUsername(), getClientBox().getPublicKey(), getClientSalt(), encryptedClientBox);
    }

    public void logout(){
        setClientBox(null);
        setUsername("");
        setClientKey(null);
    }

    //Document's Operations
    public Document_t createDocument(String documentTitle) throws Exception {
        if(documentTitle.equals("")){
            return null;
        }

        Document_t document = new Document_t(documentTitle, this.getUsername(), getClientBox().getPrivateKey());
        if(clientBox.getDocumentsIDSet().contains(document.getDocID())){
            System.out.println("Document with same title already exists");
            return null;
        }
        SecretKey key = getRandomSecretKey();
        clientBox.addDocument(document.getDocID(), document.getOwner(), key);
        uploadDocument(document, false);
        return document;

    }

    public boolean changeDocumentKey(Document_t document){
        try {
            getClientBox().changeDocumentKey(document.getDocID(), getRandomSecretKey());
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean uploadDocument(Document_t document, boolean isSharedDocument){
        try {
            if (document==null) {
                throw new NullPointerException("document is null");
            }

            SecretKey key;
            if(isSharedDocument){
                key = getClientBox().getSharedDocumentKey(document.getDocID());
            }else {
                key = getClientBox().getDocumentKey(document.getDocID());
            }
            String hashedDocID = hashInText(document.getDocID() + "&&" + document.getOwner(), null);
            byte[] encryptedDocument = encrypt(key, server.getClientSalt(document.getOwner()), document);
            server.storeDocument(hashedDocID, encryptedDocument);
            resendClientBox();
            return true;
        } catch ( NoSuchAlgorithmException | IOException | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }

    }

    public Document_t downloadDocument(String documentID, String owner, SecretKey documentKey) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, //hash exception
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, //crypto exceptions (wrong key)
            SignatureException, DocumentIntegrityCompromisedException //wrong signature exception or hashes are different
    {
        String hashedDocID = hashInText(documentID + "&&" + owner, null);
        byte[] encryptedDocument = server.getDocument(hashedDocID);

        Document_t document = (Document_t) decrypt(documentKey, server.getClientSalt(owner), encryptedDocument);

        //check document integrity
        if(!MessageDigest.isEqual(hash(document.getContent(), null),document.getContentHash()))
//        if(!hash(document.getContent(), null).equals(document.getContentHash()))
            throw new DocumentIntegrityCompromisedException();

        //check signature
        if(!verify(document.getContentHash(), getUserPublicKey(document.getLastEditor()), document.getSignature()))
            throw new SignatureException("Last editors' signature does not match");


        return document;

    }


    public boolean removeDocument(String documentID){
        try {
            getClientBox().removeDocument(documentID);
            resendClientBox();
            String hashedDocID = hashInText(documentID + "&&" + getClientBox().getOwnerID(), null);
            return server.removeDocument(hashedDocID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeSharedDocument(String documentID){
        try {
            getClientBox().removeDocument(documentID);
            resendClientBox();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // File Sharing

    // for more sensible operations like trusting the device or change the password
    public boolean doubleCheckPassword(String password){
        try {
            SecretKey secretKeyToCheck = getSecretKey(password, getClientSalt());
            return secretKeyToCheck.equals(getClientKey());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public String[] getRegisteredUsers(){
        try {

            return server.getRegisteredUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PublicKey getUserPublicKey(String clientUsername){
        try {
            return server.getUserPublicKey(clientUsername);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void shareDocument(String documentID,String targetUser) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException, SignatureException, InvalidAlgorithmParameterException {

        PublicKey clientPublicKey = getUserPublicKey(targetUser);
        EncryptedDocInfo_t encryptedDocInfo = new EncryptedDocInfo_t(
                encrypt(clientPublicKey, getClientBox().getDocumentInfo(documentID).getDocID()),
                encrypt(clientPublicKey, getClientBox().getDocumentInfo(documentID).getOwner()),
                encrypt(clientPublicKey, getClientBox().getDocumentInfo(documentID).getKey()));

        server.storeObjectInClientBin(targetUser, encryptedDocInfo, getUsername());

        getClientBox().getDocumentInfo(documentID).addPermission(new Permission_t(targetUser, true));
        resendClientBox();

    }

    public void getSharedDocuments() throws RemoteException {
        HashMap<String, List<EncryptedDocInfo_t>> binDocLists = server.getBinLists(getUsername());
        binDocLists.forEach((clientID, encryptedDocs) -> {
            encryptedDocs.forEach((EncryptedDocInfo_t encryptedInfoDoc) -> {
                DocumentInfo_t documentInfo = new DocumentInfo_t(
                        encryptedInfoDoc.getDocID(getClientBox().getPrivateKey()),
                        encryptedInfoDoc.getOwner(getClientBox().getPrivateKey()),
                        encryptedInfoDoc.getKey(getClientBox().getPrivateKey()));
                getClientBox().addSharedDocument(documentInfo);
                try {
                    resendClientBox();
                } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
                        | IOException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }
            });
        });

        try {
            Date currentTime = new Date();
            byte[] signedSerializedCurrentTime = serialize(currentTime);
            byte[] signature = sign(signedSerializedCurrentTime, getClientBox().getPrivateKey());
            server.emptyBin(getUsername(), signedSerializedCurrentTime, signature);
        } catch (IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    //TODO remove, just for testing
    public static void main(String[] args) throws Exception{



        ImplementationClient client = new ImplementationClient();
//        client.register("Hello", "helloworld");
//        client.login("Hello", "helloworld");
//        System.out.println("list of docs:");
//        client.getClientBox().print();
//
//
//        Document_t doc = client.createDocument("title example");
//        Document_t doc3 = client.createDocument("title example 3");
//        doc.setContent("example content!", client.getClientBox().getOwnerID(), client.getClientBox().getPrivateKey());
//        doc3.setContent("example content 3", client.getClientBox().getOwnerID(), client.getClientBox().getPrivateKey());
//
//        client.uploadDocument(doc, false);
//
//        client.uploadDocument(doc3, false);
//
//        doc.print();
//
//        doc3.print();
//
//
//        Document_t docServer = client.downloadDocument(doc.getDocID(), client.getUsername(), client.getClientBox().getDocumentKey(doc.getDocID()));
//        Document_t doc3Server = client.downloadDocument(doc3.getDocID(), client.getUsername(),client.getClientBox().getDocumentKey(doc3.getDocID()));
//
//        docServer.print();
//
//        doc3Server.print();
//
//        client.getClientBox().print();

        client.login("1", "1");

        client.getSharedDocuments();
        client.getClientBox().print();



    }
}
