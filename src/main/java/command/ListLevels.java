package command;

import businesslogic.LevelService;
import businesslogic.exception.MapReaderException;
import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.EnumSet;
import java.util.regex.Matcher;
import model.GamePhase;
import model.GameState;

/** Kilistázza a betölthető pályákat. */
public class ListLevels extends Command {

    private final LevelService levelService;
    private final GameEventPublisher publisher;

    public ListLevels(LevelService levelService, GameEventPublisher publisher) {
        super("levels", "levels", EnumSet.of(GamePhase.MENU, GamePhase.SOLVED));
        this.levelService = levelService;
        this.publisher = publisher;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        try {
            publisher.publish(new GameEvent(GameEventType.INFO,
                    "Pályák: " + String.join(", ", levelService.getLevelNames())));
        } catch (MapReaderException e) {
            throw new IllegalArgumentException("A pályák listája nem olvasható: " + e.getMessage(), e);
        }
    }
}
