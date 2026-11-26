import config.GameConfig;
import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import event.listener.AuditLoggerListener;
import event.listener.ConsoleEventPrinter;
import event.listener.ErrorCounter;

public class Main {

    public static void main(String[] args) {
        GameConfig config = GameConfig.getInstance();

        GameEventPublisher publisher = new GameEventPublisher();
        ErrorCounter errorCounter = new ErrorCounter();
        publisher.subscribe(new ConsoleEventPrinter());
        publisher.subscribe(new AuditLoggerListener());
        publisher.subscribe(errorCounter);

        publisher.publish(new GameEvent(GameEventType.GAME_STARTED,
                "Rush Hour - pályaméret: " + config.getBoardSize() + "x" + config.getBoardSize()));
    }
}
