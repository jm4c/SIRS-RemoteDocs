package types;

import javax.crypto.SecretKey;
import java.util.HashMap;

public class DocumentInfo_t extends Type_t{

    final private String docID;
    final private String owner;
    private SecretKey key;
    private final HashMap<String,Permission_t> permissions;

    public DocumentInfo_t(String docID, String owner, SecretKey key) {
        this.docID = docID;
        this.owner = owner;
        this.key = key;
        this.permissions = new HashMap<>();
    }

    public void print() {
        System.out.println("----DocInfo----");
        System.out.println("Doc ID: " + docID);
        System.out.println("Owner:  " + owner);
    }


    public String getDocID() {
        return docID;
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public HashMap<String,Permission_t> getPermissions() {
        return permissions;
    }

    public void addPermission(Permission_t permission) {
        permissions.put(permission.getClientID(),permission);
    }

    public void removePermission(String clientID){
        permissions.remove(clientID);
    }

    public String getOwner() {
        return owner;
    }
}
