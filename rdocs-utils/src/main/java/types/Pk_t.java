package types;

import java.security.PublicKey;

public class Pk_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private final PublicKey value;

    public Pk_t(PublicKey id) {
        this.value = id;
    }

    @Override
    public void print() {
        System.out.println();
        System.out.println("Public Key: ");
        System.out.println(value.toString());
        System.out.println();
    }

    @Override
    public PublicKey getValue() {
        return value;
    }
}
