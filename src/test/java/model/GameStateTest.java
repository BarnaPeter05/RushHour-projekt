package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import businesslogic.BoardService;
import org.junit.jupiter.api.Test;

class GameStateTest {

    @Test
    void defaultValuesAreCorrect() {
        GameState state = new GameState();

        assertEquals(GamePhase.MENU, state.getPhase());
        assertNull(state.getPlayerName());
        assertNull(state.getLevelName());
        assertNull(state.getBoardService());
        assertFalse(state.isExitRequested());
    }

    @Test
    void settersChangeValues() {
        GameState state = new GameState();
        BoardService service = new BoardService(Board.builder().withBoardSize(6)
                .addVehicle(new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL)).build());

        state.setPhase(GamePhase.PLAYING);
        state.setPlayerName("Anna");
        state.setLevelName("beginner");
        state.setBoardService(service);
        state.setExitRequested(true);

        assertEquals(GamePhase.PLAYING, state.getPhase());
        assertEquals("Anna", state.getPlayerName());
        assertEquals("beginner", state.getLevelName());
        assertSame(service, state.getBoardService());
        assertTrue(state.isExitRequested());
    }

    @Test
    void phasesAreInLifecycleOrder() {
        assertEquals(3, GamePhase.values().length);
        assertEquals(GamePhase.MENU, GamePhase.values()[0]);
        assertEquals(GamePhase.SOLVED, GamePhase.valueOf("SOLVED"));
    }
}
