package types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientBin_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private String ownerID;
    private HashMap<String, List<EncryptedDocInfo_t>> listsOfDocInfo;


    public ClientBin_t(String ownerID) {
        this.ownerID = ownerID;
        this.listsOfDocInfo = new HashMap<>();

    }

    public void addDocument(String owner, EncryptedDocInfo_t encryptedDocInfo){
        if(!listsOfDocInfo.containsKey(owner))
            listsOfDocInfo.put(owner, new ArrayList<>());
        listsOfDocInfo.get(owner).add(encryptedDocInfo);
    }

    public void emptyBin(){
        this.listsOfDocInfo = new HashMap<>();
    }

    public HashMap<String, List<EncryptedDocInfo_t>> getLists(){
        return listsOfDocInfo;
    }


    public String getOwnerID(){
        return ownerID;
    }


    @Override
    public void print() {
        System.out.println("Lists of documents in " + getOwnerID() + "'s bin:");
        if (!listsOfDocInfo.isEmpty()) {
            listsOfDocInfo.forEach((owner, key) -> System.out.println(owner + "'s shared docs"));
        } else{
            System.out.println("no Lists in " + getOwnerID() + "'s bin:");
        }
    }

}
