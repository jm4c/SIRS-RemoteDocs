package types;

import javax.crypto.SecretKey;
import java.util.HashMap;

public class DocumentInfo_t extends Type_t{

    final private String docID;
    private SecretKey key;
    private HashMap<String,Permission_t> permissions;

    public DocumentInfo_t(String docID, SecretKey key) {
        this.docID = docID;
        this.key = key;
        this.permissions = new HashMap<>();
    }

    @Override
    public void print() {
        System.out.println("----DocInfo----");
        System.out.println("Doc ID: " + docID);
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

    public Permission_t getPermission(String clientID){
        return permissions.get(clientID);
    }

    public void addPermission(Permission_t permission) {
        permissions.put(permission.getClientID(),permission);
    }

    public void removePermission(String clientID){
        permissions.remove(clientID);
    }
}
