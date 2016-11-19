package exceptions;

public class IDMismatchException extends Exception {

    private static final long serialVersionUID = 1L;

    public IDMismatchException() {
    }

    public IDMismatchException(String message) {
        super(message);
    }
}
