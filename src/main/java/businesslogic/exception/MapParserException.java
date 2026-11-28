package businesslogic.exception;

/** A beolvasott szöveg nem értelmezhető pályaként. Nem ellenőrzött kivétel, a Sudoku minta szerint. */
public class MapParserException extends RuntimeException {

    public MapParserException(String message) {
        super(message);
    }
}