package command;

import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.EnumSet;
import java.util.regex.Matcher;
import model.GamePhase;
import model.GameState;

/** Kirajzolja az aktuális pályát és az eddigi lépések számát. */
public class ShowBoard extends Command {

    private final GameEventPublisher publisher;

    public ShowBoard(GameEventPublisher publisher) {
        super("board", "board", EnumSet.of(GamePhase.PLAYING, GamePhase.SOLVED));
        this.publisher = publisher;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        publisher.publish(new GameEvent(GameEventType.INFO, state.getBoardService().getBoard()
                + "\nLépések: " + state.getBoardService().getMoveCount()));
    }
}
