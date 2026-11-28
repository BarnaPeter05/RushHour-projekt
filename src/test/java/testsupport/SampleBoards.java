package testsupport;

import java.util.List;
import model.Board;
import model.Orientation;
import model.Position;
import model.Vehicle;

/** Tesztekben használt mintapálya: megoldása B DOWN 2, majd X RIGHT 4. */
public final class SampleBoards {

    /** A mintapálya fájlformátumban. */
    public static final List<String> SAMPLE_LINES = List.of(
            "C C . . . .",
            ". . . B . .",
            "X X . B . .",
            ". . . B . D",
            ". . . . . D",
            "E E . . . D");

    /** Hibás pálya: a piros autó nem a kijárat sorában áll. */
    public static final List<String> WRONG_ROW_LINES = List.of(
            ". . . . . .",
            "X X . . . .",
            ". . . . . .",
            ". . . . . .",
            ". . . . . .",
            ". . . . . .");

    /** Hibás pálya: már a kezdetén megoldott. */
    public static final List<String> ALREADY_SOLVED_LINES = List.of(
            ". . . . . .",
            ". . . . . .",
            ". . . . X X",
            ". . . . . .",
            ". . . . . .",
            ". . . . . .");

    private SampleBoards() {
    }

    public static Board sampleBoard() {
        return Board.builder()
                .withBoardSize(6)
                .addVehicle(new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL))
                .addVehicle(new Vehicle('B', new Position(1, 3), 3, Orientation.VERTICAL))
                .addVehicle(new Vehicle('C', new Position(0, 0), 2, Orientation.HORIZONTAL))
                .addVehicle(new Vehicle('D', new Position(3, 5), 3, Orientation.VERTICAL))
                .addVehicle(new Vehicle('E', new Position(5, 0), 2, Orientation.HORIZONTAL))
                .build();
    }
}
