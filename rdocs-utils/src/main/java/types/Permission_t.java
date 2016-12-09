package types;

public class Permission_t extends Type_t{

    private final String clientID;

    public Permission_t(String clientID) {
        this.clientID = clientID;
    }

    public String getClientID() { return clientID; }


}
