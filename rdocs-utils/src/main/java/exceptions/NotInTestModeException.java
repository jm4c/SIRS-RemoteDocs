package exceptions;

public class NotInTestModeException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotInTestModeException() {
    }

    public NotInTestModeException(String message) {
        super(message);
    }
}