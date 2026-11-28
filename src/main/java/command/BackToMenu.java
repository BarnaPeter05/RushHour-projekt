package command;

import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.EnumSet;
import java.util.regex.Matcher;
import model.GamePhase;
import model.GameState;

/** Visszalép a menübe, a megkezdett pálya elmarad. */
public class BackToMenu extends Command {

    private final GameEventPublisher publisher;

    public BackToMenu(GameEventPublisher publisher) {
        super("menu", "menu", EnumSet.of(GamePhase.PLAYING, GamePhase.SOLVED));
        this.publisher = publisher;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        state.setBoardService(null);
        state.setLevelName(null);
        state.setPhase(GamePhase.MENU);
        publisher.publish(new GameEvent(GameEventType.INFO, "Vissza a menübe."));
    }
}