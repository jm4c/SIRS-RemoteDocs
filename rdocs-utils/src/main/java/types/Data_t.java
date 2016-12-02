package types;

import java.util.Arrays;

public class Data_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private byte[] value;

    public Data_t(byte[] id) {
        this.value = id;
    }

    @Override
    public void print() {
        System.out.println();
        System.out.println("Data: ");
        System.out.println(Arrays.toString(value));
        System.out.println();
    }

    public byte[] getValue() {
        return value;
    }
}
