package types;

import javax.crypto.SecretKey;
import java.util.*;

public class ClientBox_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private String clientID;
    private HashMap<String, List<Permission_t>> permissionsMap; //contains all client's docs and their respective permissions
    private HashMap<String, SecretKey> keysMap; //TODO maybe create another structure to hold both values in same hashmap? staying like this for now for simplicity
    private HashMap<String, DocumentInfo_t> documents;

    public ClientBox_t(String clientID) {
        this.clientID = clientID;
        this.permissionsMap = new HashMap<>();
        this.keysMap = new HashMap<>();
        this.documents = new HashMap<>();

    }

    public void addDocument(String documentID, SecretKey docKey){
        documents.put(documentID, new DocumentInfo_t(documentID, docKey));
    }

    public void removeDocument(String documentID){
        documents.remove(documentID);
    }

    public void changeDocumentKey(String documentID, SecretKey newKey){
        documents.get(documentID).setKey(newKey);
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
        permissionsMap.get(documentID).add(permission);

        //TODO share key with other user (Intermediate)
    }

    public Set<String> getDocumentsSet(){
        return keysMap.keySet();
    }




    @Override
    public void print() {
        if (!keysMap.isEmpty()) {
            keysMap.forEach((docID, key) -> System.out.println(docID));
        } else{
            System.out.println("no documents");
        }
    }

}
