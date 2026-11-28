package businesslogic.map.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import businesslogic.exception.MapParserException;
import java.util.List;
import java.util.Set;
import model.Board;
import model.Orientation;
import model.Position;
import model.Vehicle;
import org.junit.jupiter.api.Test;
import testsupport.SampleBoards;

class MapParserTest {

    private static final String EMPTY_ROW = ". . . . . .";

    private final MapParser parser = new MapParser(6);

    /** Hat soros pálya: a 0. és a 2. sor megadott, a többi üres. */
    private static List<String> rows(String row0, String row2) {
        return List.of(row0, EMPTY_ROW, row2, EMPTY_ROW, EMPTY_ROW, EMPTY_ROW);
    }

    @Test
    void parsesTheSampleMap() {
        Board board = parser.parseMap(SampleBoards.SAMPLE_LINES);
        Board expected = SampleBoards.sampleBoard();

        assertEquals(6, board.getBoardSize());
        assertEquals(Set.copyOf(expected.getVehicles()), Set.copyOf(board.getVehicles()));
        assertEquals(expected.getVehicles().size(), board.getVehicles().size());
    }

    @Test
    void detectsOrientationLengthAndTopLeftCell() {
        Board board = parser.parseMap(SampleBoards.SAMPLE_LINES);

        assertEquals(new Vehicle('B', new Position(1, 3), 3, Orientation.VERTICAL),
                board.findVehicle('B').orElseThrow());
        assertEquals(new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL), board.getRedCar());
    }

    @Test
    void ignoresCommentsBlankLinesAndSpacing() {
        Board board = parser.parseMap(List.of(
                "# megjegyzés", "",
                "CC....", "...B..", "  X X . B . .  ", "...B.D", ".....D", "EE...D", ""));

        assertEquals(5, board.getVehicles().size());
    }

    @Test
    void wrongRowCountIsRejected() {
        assertThrows(MapParserException.class, () -> parser.parseMap(List.of("X X . . . .")));
        assertThrows(MapParserException.class, () -> parser.parseMap(List.of()));
    }

    @Test
    void wrongColumnCountIsRejected() {
        List<String> rows = rows(EMPTY_ROW, "X X . . .");

        assertThrows(MapParserException.class, () -> parser.parseMap(rows));
    }

    @Test
    void invalidSymbolIsRejected() {
        List<String> rows = rows(EMPTY_ROW, "X X . 7 . .");

        assertThrows(MapParserException.class, () -> parser.parseMap(rows));
    }

    @Test
    void vehicleLengthMustBeTwoOrThree() {
        List<String> single = rows("A . . . . .", "X X . . . .");
        List<String> four = rows("A A A A . .", "X X . . . .");

        assertThrows(MapParserException.class, () -> parser.parseMap(single));
        assertThrows(MapParserException.class, () -> parser.parseMap(four));
    }

    @Test
    void bentOrBrokenVehicleIsRejected() {
        List<String> bent = List.of("A A . . . .", "A . . . . .", "X X . . . .", EMPTY_ROW, EMPTY_ROW, EMPTY_ROW);
        List<String> broken = rows("A . A . . .", "X X . . . .");

        assertThrows(MapParserException.class, () -> parser.parseMap(bent));
        assertThrows(MapParserException.class, () -> parser.parseMap(broken));
    }

    @Test
    void missingRedCarIsReportedAsParserError() {
        List<String> rows = rows("A A . . . .", EMPTY_ROW);

        assertThrows(MapParserException.class, () -> parser.parseMap(rows));
    }
}
