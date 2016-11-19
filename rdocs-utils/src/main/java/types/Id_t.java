package types;

import java.util.Objects;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class Id_t extends Type_t {

    private static final long serialVersionUID = 1L;
    private final String value;

    public Id_t(byte[] hash) {
        this.value = printHexBinary(hash);
    }

    @Override
    public void print() {
        System.out.println();
        System.out.println("Client ID: " + value);
        System.out.println();
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Id_t other = (Id_t) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
