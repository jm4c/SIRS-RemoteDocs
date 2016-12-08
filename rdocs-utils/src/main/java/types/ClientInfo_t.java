package types;


import java.security.PublicKey;

public class ClientInfo_t extends Type_t{

    private byte[] salt;
    private PublicKey publicKey;

    public ClientInfo_t(byte[] salt, PublicKey publicKey) {
        this.salt = salt;
        setPublicKey(publicKey);
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

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey pk){
        this.publicKey=pk;
    }
}
