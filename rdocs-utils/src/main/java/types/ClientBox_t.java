package types;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Set;

public class ClientBox_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private String ownerID;
    private HashMap<String, DocumentInfo_t> documents;
    private HashMap<String, DocumentInfo_t> sharedDocuments;

    public ClientBox_t(String ownerID) {
        this.ownerID = ownerID;
        this.documents = new HashMap<>();
        this.sharedDocuments = new HashMap<>();

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

    public void changePermission(String documentID, String clientID, String privilege){
        Permission_t permission;
        switch (privilege.toLowerCase()){
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

        //TODO share key with other user (Intermediate)
    }
    public SecretKey getDocumentKey(String documentID){
        return documents.get(documentID).getKey();
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

    public DocumentInfo_t getDocumentInfo(String docID){
        return documents.get(docID);
    }




    @Override
    public void print() {
        System.out.println("Documents in " + getOwnerID() + "'s box:");
        if (!documents.isEmpty()) {
            documents.forEach((docID, key) -> System.out.println(docID));
        } else{
            System.out.println("no documents");
        }
    }

}
