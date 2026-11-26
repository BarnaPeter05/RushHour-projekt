package businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import model.Board;
import model.Direction;
import model.Move;
import model.Orientation;
import model.Position;
import model.Vehicle;
import org.junit.jupiter.api.Test;

class BoardServiceTest {

    /**
     * <pre>
     *    0 1 2 3 4 5
     * 0  C C . . . .
     * 1  . . . B . .
     * 2  X X . B . .   &gt;
     * 3  . . . B . D
     * 4  . . . . . D
     * 5  E E . . . D
     * </pre>
     */
    private static Board sampleBoard() {
        return Board.builder()
                .withBoardSize(6)
                .addVehicle(new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL))
                .addVehicle(new Vehicle('B', new Position(1, 3), 3, Orientation.VERTICAL))
                .addVehicle(new Vehicle('C', new Position(0, 0), 2, Orientation.HORIZONTAL))
                .addVehicle(new Vehicle('D', new Position(3, 5), 3, Orientation.VERTICAL))
                .addVehicle(new Vehicle('E', new Position(5, 0), 2, Orientation.HORIZONTAL))
                .build();
    }

    private static List<String> names(List<Move> moves) {
        List<String> result = new ArrayList<>();
        for (Move move : moves) {
            result.add(move.toString());
        }
        return result;
    }

    @Test
    void constructorRejectsNullBoard() {
        assertThrows(IllegalArgumentException.class, () -> new BoardService(null));
    }

    @Test
    void getBoardReturnsTheGivenBoard() {
        Board board = sampleBoard();

        assertSame(board, new BoardService(board).getBoard());
        assertEquals(0, new BoardService(board).getMoveCount());
    }

    @Test
    void fitsChecksBoardBoundaries() {
        BoardService service = new BoardService(sampleBoard());

        assertTrue(service.fits(new Position(0, 0)));
        assertTrue(service.fits(new Position(5, 5)));
        assertFalse(service.fits(new Position(-1, 0)));
        assertFalse(service.fits(new Position(0, -1)));
        assertFalse(service.fits(new Position(6, 0)));
        assertFalse(service.fits(new Position(0, 6)));
    }

    @Test
    void isFreeRecognizesVehiclesAndOutsideCells() {
        BoardService service = new BoardService(sampleBoard());

        assertFalse(service.isFree(new Position(0, 0)));
        assertFalse(service.isFree(new Position(3, 3)));
        assertTrue(service.isFree(new Position(0, 3)));
        assertFalse(service.isFree(new Position(-1, 0)));
        assertFalse(service.isFree(new Position(6, 0)));
    }

    @Test
    void possibleMovesCoverEveryVehicleAndDistance() {
        BoardService service = new BoardService(sampleBoard());

        assertEquals(List.of(
                        "X RIGHT 1",
                        "B UP 1", "B DOWN 1", "B DOWN 2",
                        "C RIGHT 1", "C RIGHT 2", "C RIGHT 3", "C RIGHT 4",
                        "D UP 1", "D UP 2", "D UP 3",
                        "E RIGHT 1", "E RIGHT 2", "E RIGHT 3"),
                names(service.determinePossibleMoves()));
    }

    @Test
    void vehiclesCannotJumpOverOthers() {
        BoardService service = new BoardService(sampleBoard());
        List<String> moves = names(service.determinePossibleMoves());

        assertFalse(moves.contains("X RIGHT 2"));
        assertFalse(moves.contains("E RIGHT 4"));
        assertFalse(moves.contains("X UP 1"));
    }

    @Test
    void doMoveReplacesTheBoardAndCountsMoves() {
        BoardService service = new BoardService(sampleBoard());
        Board before = service.getBoard();

        service.doMove(new Move('B', Direction.DOWN, 2));

        assertNotSame(before, service.getBoard());
        assertEquals(new Position(3, 3), service.getBoard().findVehicle('B').orElseThrow().getPosition());
        assertEquals(new Position(1, 3), before.findVehicle('B').orElseThrow().getPosition());
        assertEquals(1, service.getMoveCount());
    }

    @Test
    void puzzleIsSolvedWhenRedCarReachesTheExit() {
        BoardService service = new BoardService(sampleBoard());
        assertFalse(service.isSolved());

        service.doMove(new Move('B', Direction.DOWN, 2));
        assertFalse(service.isSolved());

        service.doMove(new Move('X', Direction.RIGHT, 4));
        assertTrue(service.isSolved());
        assertEquals(2, service.getMoveCount());
    }

    @Test
    void illegalMovesAreRejectedAndChangeNothing() {
        BoardService service = new BoardService(sampleBoard());
        Board before = service.getBoard();

        assertThrows(IllegalArgumentException.class, () -> service.doMove(new Move('X', Direction.RIGHT, 2)));
        assertThrows(IllegalArgumentException.class, () -> service.doMove(new Move('X', Direction.UP, 1)));
        assertThrows(IllegalArgumentException.class, () -> service.doMove(new Move('X', Direction.LEFT, 1)));
        assertThrows(IllegalArgumentException.class, () -> service.doMove(new Move('Z', Direction.UP, 1)));
        assertThrows(IllegalArgumentException.class, () -> service.doMove(null));
        assertSame(before, service.getBoard());
        assertEquals(0, service.getMoveCount());
    }
}
