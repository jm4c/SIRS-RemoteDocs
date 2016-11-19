package types;

import java.util.Arrays;

public class Buffer_t extends Type_t {

    private static final long serialVersionUID = 1L;
    public byte[] value;

    public Buffer_t(byte[] id) {
        this.value = id;
    }

    @Override
    public void print() {
        System.out.println();
        System.out.println("Buffer:");
        System.out.println(Arrays.toString(value));
        System.out.println();
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] b) {
        this.value = b;
    }
}
