package testsupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

import businesslogic.LevelService;
import businesslogic.map.parser.MapParser;
import businesslogic.map.validation.ExitRowValidator;
import businesslogic.map.validation.NotSolvedValidator;
import command.Command;
import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import model.GameState;

/** Közös előkészítés a parancsok teszteléséhez: állapot, eseménygyűjtő és mintapályák. */
public abstract class CommandTestBase {

    protected final List<GameEvent> events = new ArrayList<>();
    protected final GameEventPublisher publisher = new GameEventPublisher();
    protected final GameState state = new GameState();
    protected final LevelService levelService = new LevelService(FakeLevelSource.standard(), new MapParser(6),
            List.of(new ExitRowValidator(2), new NotSolvedValidator()));

    protected CommandTestBase() {
        publisher.subscribe(events::add);
    }

    /** Lefuttatja a parancsot a megadott bemenettel, ahogy a GameEngine is tenné. */
    protected void run(Command command, String input) {
        Matcher matcher = command.match(input);
        assertTrue(matcher.matches(), "A bemenet nem illeszkedik: " + input);
        command.apply(state, matcher);
    }

    protected boolean anyMessageContains(GameEventType type, String text) {
        return events.stream().anyMatch(event -> event.type() == type && event.message().contains(text));
    }

    protected long count(GameEventType type) {
        return events.stream().filter(event -> event.type() == type).count();
    }
}
