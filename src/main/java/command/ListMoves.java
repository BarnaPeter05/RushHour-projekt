package command;

import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import model.GamePhase;
import model.GameState;
import model.Move;

/** Kilistázza az összes most szabályos lépést (segítség a játékhoz). */
public class ListMoves extends Command {

    private final GameEventPublisher publisher;

    public ListMoves(GameEventPublisher publisher) {
        super("moves", "moves", EnumSet.of(GamePhase.PLAYING));
        this.publisher = publisher;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        List<Move> moves = state.getBoardService().determinePossibleMoves();
        String text = moves.stream().map(Move::toString).collect(Collectors.joining(", "));
        publisher.publish(new GameEvent(GameEventType.INFO, "Lehetséges lépések: " + text));
    }
}
