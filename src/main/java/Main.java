import businesslogic.LevelService;
import command.BackToMenu;
import command.CommandRegistry;
import command.Exit;
import command.GameEngine;
import command.Help;
import command.LevelStarter;
import command.ListLevels;
import command.ListMoves;
import command.LoadLevel;
import command.MoveVehicle;
import command.Restart;
import command.SetName;
import command.ShowBoard;
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
        LevelService levelService = LevelService.createDefault();
        LevelStarter starter = new LevelStarter(levelService, publisher);

        CommandRegistry registry = new CommandRegistry();
        registry.register(new Help(publisher, registry::getCommands));
        registry.register(new SetName(publisher));
        registry.register(new ListLevels(levelService, publisher));
        registry.register(new LoadLevel(starter));
        registry.register(new ShowBoard(publisher));
        registry.register(new ListMoves(publisher));
        registry.register(new MoveVehicle(publisher));
        registry.register(new Restart(starter));
        registry.register(new BackToMenu(publisher));
        registry.register(new Exit());

        String welcome = "Rush Hour - pályaméret: " + config.getBoardSize() + "x" + config.getBoardSize();
        String hint = "Kezdéshez add meg a neved (name <játékosnév>), majd tölts be egy pályát (load <pálya>).";
        publisher.publish(new GameEvent(GameEventType.GAME_STARTED, welcome + "\n" + hint));

        new GameEngine(state, registry, publisher, new Scanner(System.in)).run();

        System.out.println("Hibás bemenetek és hibák száma: " + errorCounter.getErrorCount());
    }
}
