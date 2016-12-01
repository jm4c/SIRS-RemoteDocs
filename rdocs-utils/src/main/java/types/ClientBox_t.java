package types;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ClientBox_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private String clientID;
    private HashMap<String, List<Permissions_t>> permissionsMap; //contains all client's docs and their respective permissions
    private HashMap<String, PrivateKey> keysMap; //TODO maybe create another structure to hold both values in same hashmap? staying like this for now for simplicity

    public ClientBox_t(String clientID) {
        this.clientID = clientID;
        this.permissionsMap = new HashMap<>();
        this.keysMap = new HashMap<>();
    }

    //TODO temporary type for documentID
    public void addDocument(String documentID, PrivateKey docKey){
        permissionsMap.put(documentID, new ArrayList<>());
        keysMap.put(documentID, docKey);
    }

    public void removeDocument(String documentID){
        permissionsMap.remove(documentID);
        keysMap.remove(documentID);
    }

    public void changeDocumentKey(String documentID, PrivateKey newKey){
        keysMap.replace(documentID, newKey);
    }

    public void changePermission(String documentID, String clientID, String privilege){
        Permissions_t permission;
        switch (privilege.toLowerCase()){
            case "r":
                permission = new Permissions_t(clientID, false);
                break;
            case "rw":
                permission = new Permissions_t(clientID, true);
                break;
            default:
                permissionsMap.remove(documentID);
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

    @Override
    public byte[] getValue() {
        return null;
    }
}
