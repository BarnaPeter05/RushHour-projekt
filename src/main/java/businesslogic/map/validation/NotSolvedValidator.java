package businesslogic.map.validation;

import businesslogic.exception.MapValidationException;
import model.Board;
import model.Vehicle;

/** A pálya nem lehet már a kezdetén megoldott. */
public class NotSolvedValidator implements MapValidatorInterface {

    @Override
    public void validate(Board board) throws MapValidationException {
        Vehicle red = board.getRedCar();
        if (red.getPosition().getCol() + red.getLength() - 1 == board.getBoardSize() - 1) {
            throw new MapValidationException("A pálya már eleve megoldott: a piros autó a kijáratnál áll.");
        }
    }
}
