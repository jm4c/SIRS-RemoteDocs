package types;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    //TODO temporary placeholder for documentID
    public void addDocument(Id_t documentID, PrivateKey docKey){
        permissionsMap.put(documentID.getValue(), new ArrayList<>());
        keysMap.put(documentID.getValue(), docKey);
    }

    public void removeDocument(Id_t documentID){
        permissionsMap.remove(documentID.getValue());
        keysMap.remove(documentID.getValue());
    }

    public void changeDocumentKey(Id_t documentID, PrivateKey newKey){
        keysMap.replace(documentID.getValue(), newKey);
    }

    public void changePermission(Id_t documentID, String clientID, String privilege){
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




    @Override
    public void print() {
    }

    @Override
    public byte[] getValue() {
        return null;
    }
}
