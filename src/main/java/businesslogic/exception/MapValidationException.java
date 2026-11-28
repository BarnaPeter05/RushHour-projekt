package businesslogic.exception;

/** A pálya értelmezhető, de játékszabály szerint nem megfelelő. */
public class MapValidationException extends Exception {

    public MapValidationException(String message) {
        super(message);
    }
}
