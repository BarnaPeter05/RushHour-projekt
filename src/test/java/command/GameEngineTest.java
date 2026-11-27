package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import model.GamePhase;
import model.GameState;
import org.junit.jupiter.api.Test;

class GameEngineTest {

    /** Olyan parancs, ami végrehajtáskor kivételt dob. */
    private static final class FailingCommand extends Command {

        private final RuntimeException failure;

        FailingCommand(String name, RuntimeException failure) {
            super(name, name, Set.of(GamePhase.MENU));
            this.failure = failure;
        }

        @Override
        public void apply(GameState state, Matcher matcher) {
            throw failure;
        }
    }

    private final List<GameEvent> events = new ArrayList<>();
    private final GameState state = new GameState();
    private final StubCommand play = new StubCommand("play", "play (\\d+)", Set.of(GamePhase.PLAYING));

    private void runEngine(String input) {
        GameEventPublisher publisher = new GameEventPublisher();
        publisher.subscribe(events::add);
        CommandRegistry registry = new CommandRegistry();
        registry.register(new Help(publisher, registry::getCommands));
        registry.register(new Exit());
        registry.register(play);
        registry.register(new FailingCommand("bad", new IllegalArgumentException("rossz lépés")));
        registry.register(new FailingCommand("crash", new IllegalStateException("összeomlás")));
        registry.register(new FailingCommand("boom", new UnsupportedOperationException("bumm")));
        new GameEngine(state, registry, publisher, new Scanner(input)).run();
    }

    private long count(GameEventType type) {
        return events.stream().filter(event -> event.type() == type).count();
    }

    private boolean anyMessageContains(GameEventType type, String text) {
        return events.stream().anyMatch(event -> event.type() == type && event.message().contains(text));
    }

    @Test
    void exitStopsTheLoop() {
        runEngine("exit\n");

        assertTrue(state.isExitRequested());
        assertTrue(anyMessageContains(GameEventType.INFO, "Kilépés"));
    }

    @Test
    void availableCommandsAreListedEveryRound() {
        runEngine("help\nexit\n");

        assertTrue(anyMessageContains(GameEventType.INFO, "Elérhető parancsok: help, exit"));
    }

    @Test
    void helpCommandIsExecuted() {
        runEngine("help\nexit\n");

        assertEquals(1, events.stream().filter(e -> e.message().contains("(MENU):")).count());
    }

    @Test
    void unknownCommandIsReported() {
        runEngine("bogus\nexit\n");

        assertEquals(1, count(GameEventType.INVALID_COMMAND));
        assertTrue(anyMessageContains(GameEventType.INVALID_COMMAND, "Ismeretlen parancs: bogus"));
    }

    @Test
    void commandOfAnotherPhaseIsNotAvailable() {
        runEngine("play 3\nexit\n");

        assertTrue(anyMessageContains(GameEventType.INVALID_COMMAND, "nem érhető el"));
        assertEquals(0, play.getApplyCount());
    }

    @Test
    void commandIsAppliedInItsPhase() {
        state.setPhase(GamePhase.PLAYING);

        runEngine("play 3\nplay 4\nexit\n");

        assertEquals(2, play.getApplyCount());
        assertEquals(0, count(GameEventType.INVALID_COMMAND));
    }

    @Test
    void blankLinesAreIgnored() {
        runEngine("\n   \nexit\n");

        assertEquals(0, count(GameEventType.INVALID_COMMAND));
        assertTrue(state.isExitRequested());
    }

    @Test
    void endOfInputStopsTheEngine() {
        runEngine("");

        assertTrue(state.isExitRequested());
    }

    @Test
    void ruleViolationsAreReportedAsInvalidMoveAndTheLoopContinues() {
        runEngine("bad\ncrash\nexit\n");

        assertEquals(2, count(GameEventType.INVALID_MOVE));
        assertTrue(anyMessageContains(GameEventType.INVALID_MOVE, "rossz lépés"));
        assertTrue(anyMessageContains(GameEventType.INVALID_MOVE, "összeomlás"));
        assertTrue(state.isExitRequested());
    }

    @Test
    void unexpectedFailuresAreReportedAsErrorAndTheLoopContinues() {
        runEngine("boom\nexit\n");

        assertEquals(1, count(GameEventType.ERROR));
        assertTrue(anyMessageContains(GameEventType.ERROR, "bumm"));
        assertTrue(state.isExitRequested());
    }
}
