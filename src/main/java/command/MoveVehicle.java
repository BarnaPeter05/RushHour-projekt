package command;

import businesslogic.BoardService;
import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.EnumSet;
import java.util.Locale;
import java.util.regex.Matcher;
import model.Direction;
import model.GamePhase;
import model.GameState;
import model.Move;

/** Egy jármű csúsztatása: move &lt;jármű&gt; &lt;up|down|left|right&gt; &lt;lépés&gt;. */
public class MoveVehicle extends Command {

    private final GameEventPublisher publisher;

    public MoveVehicle(GameEventPublisher publisher) {
        super("move", "move\\s+([a-z])\\s+(up|down|left|right)\\s+(\\d{1,2})", EnumSet.of(GamePhase.PLAYING));
        this.publisher = publisher;
    }

    @Override
    public String getSyntax() {
        return "move <jármű> <up|down|left|right> <lépés>";
    }

    /** @throws IllegalArgumentException ha a lépés szabálytalan (a GameEngine hibaüzenetként mutatja meg) */
    @Override
    public void apply(GameState state, Matcher matcher) {
        char id = Character.toUpperCase(matcher.group(1).charAt(0));
        Direction direction = Direction.valueOf(matcher.group(2).toUpperCase(Locale.ROOT));
        int steps = Integer.parseInt(matcher.group(3));
        BoardService service = state.getBoardService();

        service.doMove(new Move(id, direction, steps));

        publisher.publish(new GameEvent(GameEventType.MOVE_DONE,
                "Lépés: " + id + " " + direction + " " + steps + " (összesen: " + service.getMoveCount() + ")\n"
                        + service.getBoard()));
    }
}