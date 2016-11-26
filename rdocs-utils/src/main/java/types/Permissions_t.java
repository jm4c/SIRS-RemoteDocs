package types;

public class Permissions_t extends Type_t{

    private final String clientID;
    private final boolean canWrite; // if true Read/Write if false Read Only

    public Permissions_t(String clientID, boolean writePermission) {
        this.clientID = clientID;
        this.canWrite = writePermission;
    }

    public String getClientID() { return clientID; }
    public boolean isReadOnly() { return !canWrite; }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Permissions_t)) return false;
        Permissions_t otherPermission = (Permissions_t) o;
        return this.clientID.equals(otherPermission.getClientID()) &&
                this.canWrite == !otherPermission.isReadOnly();
    }


    @Override
    public void print() {

    }

    @Override
    public byte[] getValue() {
        return null;
    }
}
