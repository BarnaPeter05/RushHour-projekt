package businesslogic.exception;

/** A pálya (vagy a pályalista) beolvasása nem sikerült. */
public class MapReaderException extends Exception {

    public MapReaderException(String message) {
        super(message);
    }

    public MapReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
