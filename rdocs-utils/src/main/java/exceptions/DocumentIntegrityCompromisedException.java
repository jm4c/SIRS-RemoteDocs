package exceptions;


public class DocumentIntegrityCompromisedException extends Exception {
    private static final long serialVersionUID = 1L;

    public DocumentIntegrityCompromisedException() {
    }

    public DocumentIntegrityCompromisedException(String message) {
        super(message);
    }
}
