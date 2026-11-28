package businesslogic.map.validation;

import businesslogic.exception.MapValidationException;
import model.Board;

/** A piros autónak a kijárat sorában kell állnia. */
public class ExitRowValidator implements MapValidatorInterface {

    private final int exitRow;

    public ExitRowValidator(int exitRow) {
        this.exitRow = exitRow;
    }

    @Override
    public void validate(Board board) throws MapValidationException {
        int redRow = board.getRedCar().getPosition().getRow();
        if (redRow != exitRow) {
            throw new MapValidationException("A piros autónak a(z) " + exitRow
                    + ". sorban kell állnia (0-tól számolva), de a(z) " + redRow + ". sorban áll.");
        }
    }
}
