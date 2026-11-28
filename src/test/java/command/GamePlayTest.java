package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.GameEventType;
import java.util.Scanner;
import model.GamePhase;
import org.junit.jupiter.api.Test;
import testsupport.CommandTestBase;

/** Teljes játékmenetek a GameEngine-en át, ugyanazzal a parancskészlettel, mint a Main-ben. */
class GamePlayTest extends CommandTestBase {

    private void play(String script) {
        LevelStarter starter = new LevelStarter(levelService, publisher);
        CommandRegistry registry = new CommandRegistry();
        registry.register(new Help(publisher, registry::getCommands));
        registry.register(new SetName(publisher));
        registry.register(new ListLevels(levelService, publisher));
        registry.register(new LoadLevel(starter));
        registry.register(new ShowBoard(publisher));
        registry.register(new ListMoves(publisher));
        registry.register(new MoveVehicle(publisher));
        registry.register(new Restart(starter));
        registry.register(new BackToMenu(publisher));
        registry.register(new Exit());
        new GameEngine(state, registry, publisher, new Scanner(script)).run();
    }

    @Test
    void aWholeGameCanBePlayedAndSolved() {
        play("name Anna\nlevels\nload sample\nboard\nmoves\nmove B down 2\nmove X right 4\nexit\n");

        assertEquals(GamePhase.SOLVED, state.getPhase());
        assertTrue(anyMessageContains(GameEventType.GAME_ENDED, "Anna"));
        assertTrue(anyMessageContains(GameEventType.GAME_ENDED, "sample pályát 2 lépésből"));
        assertEquals(0, count(GameEventType.INVALID_COMMAND));
        assertEquals(0, count(GameEventType.INVALID_MOVE));
        assertEquals(0, count(GameEventType.ERROR));
    }

    @Test
    void commandsOutsideTheirPhaseAreRefused() {
        play("move B down 1\nload sample\nexit\n");

        assertEquals(1, count(GameEventType.INVALID_COMMAND));
        assertEquals(1, count(GameEventType.INVALID_MOVE));
        assertTrue(anyMessageContains(GameEventType.INVALID_MOVE, "Előbb add meg a neved"));
        assertEquals(GamePhase.MENU, state.getPhase());
    }

    @Test
    void illegalMovesAreReportedAndTheGameContinues() {
        play("name Anna\nload sample\nmove X right 3\nmove B down 2\nmove X right 4\nexit\n");

        assertEquals(1, count(GameEventType.INVALID_MOVE));
        assertTrue(anyMessageContains(GameEventType.INVALID_MOVE, "Szabálytalan lépés"));
        assertTrue(anyMessageContains(GameEventType.GAME_ENDED, "2 lépésből"));
        assertEquals(GamePhase.SOLVED, state.getPhase());
    }

    @Test
    void aSolvedGameCanBeRestartedOrAbandoned() {
        play("name Anna\nload sample\nmove B down 2\nmove X right 4\nrestart\nboard\nmenu\nexit\n");

        assertEquals(GamePhase.MENU, state.getPhase());
        assertTrue(anyMessageContains(GameEventType.INFO, "Lépések: 0"));
        assertEquals(1, count(GameEventType.GAME_ENDED));
    }

    @Test
    void anotherLevelCanBeLoadedAfterSolving() {
        play("name Anna\nload sample\nmove B down 2\nmove X right 4\nload sample\nexit\n");

        assertEquals(GamePhase.PLAYING, state.getPhase());
        assertEquals(0, state.getBoardService().getMoveCount());
    }
}