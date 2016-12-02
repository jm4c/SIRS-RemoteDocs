package types;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class ClientInfo_t extends Type_t{

    private byte[] salt;
    private List<PublicKey> publicKeys;

    public ClientInfo_t(byte[] salt) {
        this.salt = salt;
        this.publicKeys = new ArrayList<PublicKey>();
    }

    @Override
    public void print() {

    }


    public byte[] getSalt() {
        return salt;
    }

    // Used if user changes password
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public List<PublicKey> getPublicKeys() {
        return publicKeys;
    }

    public void addPublicKey(PublicKey pk){
        publicKeys.add(pk);
    }
}
