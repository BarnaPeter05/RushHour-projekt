import command.CommandRegistry;
import command.Exit;
import command.GameEngine;
import command.Help;
import config.GameConfig;
import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import event.listener.AuditLoggerListener;
import event.listener.ConsoleEventPrinter;
import event.listener.ErrorCounter;
import java.util.Scanner;
import model.GameState;

public class Main {

    public static void main(String[] args) {
        GameConfig config = GameConfig.getInstance();

        GameEventPublisher publisher = new GameEventPublisher();
        ErrorCounter errorCounter = new ErrorCounter();
        publisher.subscribe(new ConsoleEventPrinter());
        publisher.subscribe(new AuditLoggerListener());
        publisher.subscribe(errorCounter);

        GameState state = new GameState();
        CommandRegistry registry = new CommandRegistry();
        registry.register(new Help(publisher, registry::getCommands));
        registry.register(new Exit());

        publisher.publish(new GameEvent(GameEventType.GAME_STARTED,
                "Rush Hour - pályaméret: " + config.getBoardSize() + "x" + config.getBoardSize()));

        new GameEngine(state, registry, publisher, new Scanner(System.in)).run();

        System.out.println("Hibás bemenetek és hibák száma: " + errorCounter.getErrorCount());
    }
}