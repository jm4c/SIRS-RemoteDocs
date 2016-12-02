package types;

import exceptions.NotInTestModeException;

import java.sql.Timestamp;
import java.util.List;

public class Header_t extends Type_t {

    private final static boolean TEST_MODE = true;
    private static final long serialVersionUID = 1L;
    private final List<Id_t> fileList;
    private Timestamp timestamp;

    public Header_t(List<Id_t> fileList) {
        this.fileList = fileList;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public void print() {
        System.out.println();
        System.out.println("File List: " + fileList.toString());
        System.out.println("Timestamp: " + timestamp.toString());
        System.out.println();

    }

    public List<Id_t> getValue() {
        return fileList;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) throws NotInTestModeException {
        if (TEST_MODE)
            this.timestamp = timestamp;
        else
            throw new NotInTestModeException("Not authorized to set custom timestamps outside TEST_MODE");

    }


}