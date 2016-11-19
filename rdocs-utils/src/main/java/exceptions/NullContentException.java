package exceptions;

public class NullContentException extends Exception {

    private static final long serialVersionUID = 1L;

    public NullContentException() {
    }

    public NullContentException(String message) {
        super(message);
    }
}
