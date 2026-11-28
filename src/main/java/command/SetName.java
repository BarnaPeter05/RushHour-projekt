package command;

import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.EnumSet;
import java.util.regex.Matcher;
import model.GamePhase;
import model.GameState;

/** A játékos nevének megadása (a ranglistához kell). */
public class SetName extends Command {

    private static final int MAX_LENGTH = 20;

    private final GameEventPublisher publisher;

    public SetName(GameEventPublisher publisher) {
        super("name", "name\\s+(\\S.*)", EnumSet.of(GamePhase.MENU, GamePhase.SOLVED));
        this.publisher = publisher;
    }

    @Override
    public String getSyntax() {
        return "name <játékosnév>";
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        String name = matcher.group(1).trim();
        if (name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("A név legfeljebb " + MAX_LENGTH + " karakter lehet.");
        }
        state.setPlayerName(name);
        publisher.publish(new GameEvent(GameEventType.INFO, "Üdv, " + name + "!"));
    }
}
