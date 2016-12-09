package sirs.remotedocs;

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

    private static InterfaceServer server;
    private String clientUsername;
    private ClientBox_t clientBox;
    private byte[] clientSalt;
    private SecretKey clientKey;


    public ImplementationClient() {
        try {
            connectToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return clientUsername;
    }

    private void setUsername(String username) {
        clientUsername = username;
    }

    private SecretKey getClientKey() {
        return clientKey;
    }

    private void setClientKey(SecretKey secretKey) {
        this.clientKey = secretKey;
    }

    private byte[] getClientSalt() {
        return clientSalt;
    }

    private void setClientSalt(byte[] clientSalt) {
        this.clientSalt = clientSalt;
    }

    public ClientBox_t getClientBox(){
        return clientBox;
    }

    private void setClientBox(ClientBox_t clientBox) {
        this.clientBox = clientBox;
    }

    public boolean connectToServer() throws Exception{
        Registry myReg = LocateRegistry.getRegistry("localhost");
        server = (InterfaceServer) myReg.lookup("rdocs.Server");
        System.out.println(server.greeting() + "\n");
        return isConnected();
    }

    private boolean isConnected(){
        return server != null;
    }

    //Account's Operations
    public int register(String username, String password) {
        if (username.length() < 3 || username.length() > 21) {
            System.out.println("Username must be between 4 and 20 characters long.");
            return 2;
        }

        if (password.length() < 7) {
            System.out.println("Password must be at least 8 characters long.");
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
                System.out.println("DATA RECEIVED (encrypted client box): " + encryptedBox.toString() + "\n");

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

    private void resendClientBox() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
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
                // to fix the document "stealing bug" by non-users, it is tested before uploading
                Document_t testPermissionDocument = downloadDocument(
                        document.getDocID(),
                        getClientBox().getSharedDocumentInfo(document.getDocID()).getOwner(),
                        getClientBox().getSharedDocumentKey(document.getDocID()));
                if(testPermissionDocument == null)
                    return false;
                key = getClientBox().getSharedDocumentKey(document.getDocID());
            }else {
                key = getClientBox().getDocumentKey(document.getDocID());
            }
            String hashedDocID = hashInText(document.getDocID() + "&&" + document.getOwner(), null);
            byte[] encryptedDocument = encrypt(key, server.getClientSalt(document.getOwner()), document);
            server.storeDocument(hashedDocID, encryptedDocument);
            resendClientBox();
            return true;
        } catch ( NoSuchAlgorithmException | IOException | NoSuchPaddingException | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | InvalidAlgorithmParameterException | NullPointerException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    public Document_t downloadDocument(String documentID, String owner, SecretKey documentKey) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, //other exceptions
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {  //crypto exceptions (wrong key)

        String hashedDocID = hashInText(documentID + "&&" + owner, null);
        byte[] encryptedDocument = server.getDocument(hashedDocID);

        Document_t document = (Document_t) decrypt(documentKey, server.getClientSalt(owner), encryptedDocument);

        //check document integrity
        if(!MessageDigest.isEqual(hash(document.getContent(), null),document.getContentHash()))
            document.setIntegrityFaultFlag();
        else
            //check signature
            try {
                if(!verify(document.getContentHash(), getUserPublicKey(document.getLastEditor()), document.getSignature()))
                    document.setSignatureFaultFlag();
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
                document.setSignatureFaultFlag();
            }

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

    // File Sharing

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

        getClientBox().getDocumentInfo(documentID).addPermission(new Permission_t(targetUser));
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

}
