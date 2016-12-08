package types;

import utils.CryptoUtils;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Set;

public class ClientBox_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private String ownerID;
    private KeyPair keyPair;
    private HashMap<String, DocumentInfo_t> documents;
    private HashMap<String, DocumentInfo_t> sharedDocuments;

    public ClientBox_t(String ownerID) {
        this.ownerID = ownerID;
        this.documents = new HashMap<>();
        this.sharedDocuments = new HashMap<>();
        this.keyPair = CryptoUtils.generateKeyPair();
    }

    public SecretKey getDocumentKey(String documentID){
        return documents.get(documentID).getKey();
    }
    public SecretKey getSharedDocumentKey(String documentID){
        return sharedDocuments.get(documentID).getKey();
    }

    public Set<String> getDocumentsIDSet(){
        return documents.keySet();
    }
    public Set<String> getSharedDocumentsIDSet(){
        return sharedDocuments.keySet();
    }


    public String getOwnerID(){
        return ownerID;
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public PublicKey getPublicKey(){
        return keyPair.getPublic();
    }

    public DocumentInfo_t getDocumentInfo(String docID){
        return documents.get(docID);
    }

    public DocumentInfo_t getSharedDocumentInfo(String docID){
        return sharedDocuments.get(docID);
    }

    public void addDocument(String documentID, String owner, SecretKey docKey){
        documents.put(documentID, new DocumentInfo_t(documentID, owner, docKey));
    }

    public void removeDocument(String documentID){
        documents.remove(documentID);
    }

    public void changeDocumentKey(String documentID, SecretKey newKey){
        documents.get(documentID).setKey(newKey);
    }

    public void addSharedDocument(DocumentInfo_t documentInfo){
        sharedDocuments.put(documentInfo.getDocID(), documentInfo);
    }




    public void changePermission(String documentID, String clientID, String permissionLevel){
        Permission_t permission;
        switch (permissionLevel.toLowerCase()){
            case "r":
                permission = new Permission_t(clientID, false);
                break;
            case "rw":
                permission = new Permission_t(clientID, true);
                break;
            default:
                documents.get(documentID).removePermission(clientID);
                return;
        }
        documents.get(documentID).addPermission(permission);
    }

    @Override
    public void print() {
        System.out.println("\n-----" + getOwnerID() + "'s Client Box------");
        System.out.println("-" + getOwnerID() + "'s docs:");
        if (!documents.isEmpty()) {
            documents.forEach((docID, key) -> System.out.println(docID));
        } else{
            System.out.println("no documents");
        }
        System.out.println("\n-Shared docs:");
        if (!sharedDocuments.isEmpty()) {
            sharedDocuments.forEach((docID, key) -> System.out.println(docID));
        } else{
            System.out.println("no documents");
        }
    }
}
