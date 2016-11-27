package blocks;

import java.io.Serializable;
import java.security.PublicKey;

import types.Data_t;

public class PublicKeyBlock implements Serializable {

    private static final long serialVersionUID = 1L;
    private PublicKey blockPKey;
    private byte[] blockSig;
    private final Data_t blockData;

    public PublicKeyBlock(Data_t data) {
        this.blockData = data;
    }

    public PublicKeyBlock(Data_t data, byte[] sig, PublicKey pkey) {
        this.blockPKey = pkey;
        this.blockSig = sig;
        this.blockData = data;
    }

    public PublicKey getPKey() {
        return blockPKey;
    }

    public byte[] getSig() {
        return blockSig;
    }

    public Data_t getData() {
        return blockData;
    }
}
