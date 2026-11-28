package businesslogic.map.validation;

import businesslogic.exception.MapValidationException;
import model.Board;

/** Egy játékszabály-ellenőrzés a beolvasott pályán. */
public interface MapValidatorInterface {

    void validate(Board board) throws MapValidationException;
}
