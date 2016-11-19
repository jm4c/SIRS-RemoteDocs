package types;

import java.util.Arrays;

public class Sig_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private final byte[] value;

    public Sig_t(byte[] id) {
        this.value = id;
    }

    @Override
    public void print() {
        System.out.println();
        System.out.println("Signature: ");
        System.out.println(Arrays.toString(value));
        System.out.println();
    }

    @Override
    public byte[] getValue() {
        return value;
    }
}
