package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import businesslogic.LevelService;
import businesslogic.exception.MapReaderException;
import businesslogic.map.LevelSource;
import businesslogic.map.reader.MapReaderInterface;
import event.GameEventType;
import java.util.List;
import java.util.regex.Matcher;
import model.GamePhase;
import org.junit.jupiter.api.Test;
import testsupport.CommandTestBase;

class LevelCommandsTest extends CommandTestBase {

    private final LevelStarter starter = new LevelStarter(levelService, publisher);

    @Test
    void listLevelsPublishesTheNames() {
        run(new ListLevels(levelService, publisher), "levels");

        assertTrue(anyMessageContains(GameEventType.INFO, "Pályák: sample, wrongrow, solved"));
    }

    @Test
    void listLevelsReportsReaderFailures() {
        LevelSource failing = new LevelSource() {
            @Override
            public List<String> listLevelNames() throws MapReaderException {
                throw new MapReaderException("lemezhiba");
            }

            @Override
            public MapReaderInterface open(String levelName) throws MapReaderException {
                throw new MapReaderException("lemezhiba");
            }
        };
        ListLevels command = new ListLevels(new LevelService(failing, null, List.of()), publisher);
        Matcher matcher = command.match("levels");
        assertTrue(matcher.matches());

        assertThrows(IllegalArgumentException.class, () -> command.apply(state, matcher));
    }

    @Test
    void loadRequiresAPlayerName() {
        LoadLevel load = new LoadLevel(starter);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> run(load, "load sample"));

        assertTrue(exception.getMessage().contains("name"));
        assertEquals(GamePhase.MENU, state.getPhase());
    }

    @Test
    void loadStartsTheGame() {
        state.setPlayerName("Anna");

        run(new LoadLevel(starter), "load SAMPLE");

        assertEquals(GamePhase.PLAYING, state.getPhase());
        assertEquals("sample", state.getLevelName());
        assertNotNull(state.getBoardService());
        assertEquals(0, state.getBoardService().getMoveCount());
        assertTrue(anyMessageContains(GameEventType.INFO, "Pálya betöltve: sample"));
    }

    @Test
    void loadRejectsUnknownAndBrokenLevels() {
        state.setPlayerName("Anna");
        LoadLevel load = new LoadLevel(starter);

        assertThrows(IllegalArgumentException.class, () -> run(load, "load nincsilyen"));
        IllegalArgumentException wrongRow =
                assertThrows(IllegalArgumentException.class, () -> run(load, "load wrongrow"));
        assertTrue(wrongRow.getMessage().contains("piros"));
        assertThrows(IllegalArgumentException.class, () -> run(load, "load solved"));
        assertEquals(GamePhase.MENU, state.getPhase());
        assertNull(state.getBoardService());
    }

    @Test
    void restartResetsTheLevel() {
        state.setPlayerName("Anna");
        run(new LoadLevel(starter), "load sample");
        state.getBoardService().doMove(state.getBoardService().determinePossibleMoves().get(0));
        assertEquals(1, state.getBoardService().getMoveCount());
        state.setPhase(GamePhase.SOLVED);

        run(new Restart(starter), "restart");

        assertEquals(GamePhase.PLAYING, state.getPhase());
        assertEquals(0, state.getBoardService().getMoveCount());
        assertEquals("sample", state.getLevelName());
    }

    @Test
    void backToMenuClearsTheGame() {
        state.setPlayerName("Anna");
        run(new LoadLevel(starter), "load sample");

        run(new BackToMenu(publisher), "menu");

        assertEquals(GamePhase.MENU, state.getPhase());
        assertNull(state.getBoardService());
        assertNull(state.getLevelName());
        assertEquals("Anna", state.getPlayerName());
    }

    @Test
    void commandsAreAvailableInTheRightPhases() {
        LoadLevel load = new LoadLevel(starter);
        Restart restart = new Restart(starter);
        BackToMenu menu = new BackToMenu(publisher);
        ListLevels levels = new ListLevels(levelService, publisher);

        assertTrue(load.isApplicableInPhase(GamePhase.MENU));
        assertTrue(load.isApplicableInPhase(GamePhase.SOLVED));
        assertFalse(load.isApplicableInPhase(GamePhase.PLAYING));
        assertTrue(levels.isApplicableInPhase(GamePhase.MENU));
        assertFalse(levels.isApplicableInPhase(GamePhase.PLAYING));
        assertFalse(restart.isApplicableInPhase(GamePhase.MENU));
        assertTrue(restart.isApplicableInPhase(GamePhase.PLAYING));
        assertFalse(menu.isApplicableInPhase(GamePhase.MENU));
        assertEquals("load <pálya>", load.getSyntax());
    }
}
