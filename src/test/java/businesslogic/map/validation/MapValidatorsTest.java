package businesslogic.map.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import businesslogic.exception.MapValidationException;
import model.Board;
import model.Orientation;
import model.Position;
import model.Vehicle;
import org.junit.jupiter.api.Test;
import testsupport.SampleBoards;

class MapValidatorsTest {

    private static Board redAt(int row, int col) {
        return Board.builder().withBoardSize(6)
                .addVehicle(new Vehicle('X', new Position(row, col), 2, Orientation.HORIZONTAL)).build();
    }

    @Test
    void redCarOnTheExitRowIsAccepted() {
        assertDoesNotThrow(() -> new ExitRowValidator(2).validate(SampleBoards.sampleBoard()));
    }

    @Test
    void redCarOffTheExitRowIsRejected() {
        ExitRowValidator validator = new ExitRowValidator(2);

        assertThrows(MapValidationException.class, () -> validator.validate(redAt(1, 0)));
        assertThrows(MapValidationException.class, () -> validator.validate(redAt(4, 2)));
    }

    @Test
    void unsolvedBoardIsAccepted() {
        assertDoesNotThrow(() -> new NotSolvedValidator().validate(SampleBoards.sampleBoard()));
    }

    @Test
    void alreadySolvedBoardIsRejected() {
        assertThrows(MapValidationException.class, () -> new NotSolvedValidator().validate(redAt(2, 4)));
    }
}