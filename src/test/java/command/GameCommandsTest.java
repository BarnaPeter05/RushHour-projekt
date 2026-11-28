package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import businesslogic.BoardService;
import event.GameEventType;
import model.GamePhase;
import model.Position;
import org.junit.jupiter.api.Test;
import testsupport.CommandTestBase;
import testsupport.SampleBoards;

class GameCommandsTest extends CommandTestBase {

    private void startSampleGame() {
        state.setPhase(GamePhase.PLAYING);
        state.setPlayerName("Anna");
        state.setLevelName("sample");
        state.setBoardService(new BoardService(SampleBoards.sampleBoard()));
    }

    private Position positionOf(char vehicleId) {
        return state.getBoardService().getBoard().findVehicle(vehicleId).orElseThrow().getPosition();
    }

    @Test
    void setNameStoresTheTrimmedName() {
        run(new SetName(publisher), "name    Kiss Anna  ");

        assertEquals("Kiss Anna", state.getPlayerName());
        assertTrue(anyMessageContains(GameEventType.INFO, "Üdv, Kiss Anna!"));
    }

    @Test
    void setNameRejectsTooLongNames() {
        SetName command = new SetName(publisher);

        assertThrows(IllegalArgumentException.class, () -> run(command, "name " + "a".repeat(21)));
        assertNull(state.getPlayerName());
    }

    @Test
    void setNameNeedsAnArgumentAndIsNotAvailableWhilePlaying() {
        SetName command = new SetName(publisher);

        assertFalse(command.match("name").matches());
        assertFalse(command.match("name   ").matches());
        assertTrue(command.isApplicableInPhase(GamePhase.MENU));
        assertFalse(command.isApplicableInPhase(GamePhase.PLAYING));
        assertEquals("name <játékosnév>", command.getSyntax());
    }

    @Test
    void showBoardPublishesBoardAndMoveCount() {
        startSampleGame();

        run(new ShowBoard(publisher), "board");

        assertTrue(anyMessageContains(GameEventType.INFO, "Lépések: 0"));
        assertTrue(anyMessageContains(GameEventType.INFO, "X X"));
    }

    @Test
    void listMovesPublishesAllPossibleMoves() {
        startSampleGame();

        run(new ListMoves(publisher), "moves");

        assertTrue(anyMessageContains(GameEventType.INFO, "Lehetséges lépések: X RIGHT 1, B UP 1"));
    }

    @Test
    void moveVehicleSlidesTheVehicle() {
        startSampleGame();

        run(new MoveVehicle(publisher), "move B down 2");

        assertEquals(new Position(3, 3), positionOf('B'));
        assertEquals(1, state.getBoardService().getMoveCount());
        assertTrue(anyMessageContains(GameEventType.MOVE_DONE, "B DOWN 2 (összesen: 1)"));
    }

    @Test
    void moveVehicleIgnoresCase() {
        startSampleGame();

        run(new MoveVehicle(publisher), "MOVE b Down 1");

        assertEquals(new Position(2, 3), positionOf('B'));
    }

    @Test
    void illegalMovesAreRejectedAndChangeNothing() {
        startSampleGame();
        MoveVehicle move = new MoveVehicle(publisher);

        assertThrows(IllegalArgumentException.class, () -> run(move, "move X right 3"));
        assertThrows(IllegalArgumentException.class, () -> run(move, "move X up 1"));
        assertThrows(IllegalArgumentException.class, () -> run(move, "move Z up 1"));
        assertThrows(IllegalArgumentException.class, () -> run(move, "move B down 0"));
        assertEquals(0, state.getBoardService().getMoveCount());
        assertEquals(0, count(GameEventType.MOVE_DONE));
    }

    @Test
    void moveSyntaxIsChecked() {
        MoveVehicle move = new MoveVehicle(publisher);

        assertTrue(move.match("move B down 2").matches());
        assertFalse(move.match("move 5 down 2").matches());
        assertFalse(move.match("move B sideways 2").matches());
        assertFalse(move.match("move B down 100").matches());
        assertFalse(move.match("move B down").matches());
    }

    @Test
    void gameCommandsAreOnlyAvailableWhilePlaying() {
        MoveVehicle move = new MoveVehicle(publisher);
        ListMoves moves = new ListMoves(publisher);
        ShowBoard board = new ShowBoard(publisher);

        assertTrue(move.isApplicableInPhase(GamePhase.PLAYING));
        assertFalse(move.isApplicableInPhase(GamePhase.MENU));
        assertFalse(move.isApplicableInPhase(GamePhase.SOLVED));
        assertTrue(moves.isApplicableInPhase(GamePhase.PLAYING));
        assertFalse(moves.isApplicableInPhase(GamePhase.SOLVED));
        assertTrue(board.isApplicableInPhase(GamePhase.SOLVED));
        assertFalse(board.isApplicableInPhase(GamePhase.MENU));
    }
}

