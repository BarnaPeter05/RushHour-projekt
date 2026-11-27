package command;

import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import model.GameState;

/**
 * A parancssoros főciklus (REPL). Minden körben kiírja az elérhető parancsokat, beolvas egy sort,
 * megkeresi az elsőt, amelyre a szintaxis és a fázis is illik, és végrehajtja.
 */
public class GameEngine implements Runnable {

    private final GameState state;
    private final CommandRegistry registry;
    private final GameEventPublisher publisher;
    private final Scanner scanner;

    public GameEngine(GameState state, CommandRegistry registry, GameEventPublisher publisher, Scanner scanner) {
        this.state = state;
        this.registry = registry;
        this.publisher = publisher;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        while (!state.isExitRequested()) {
            showAvailableCommands();
            if (scanner.hasNextLine()) {
                handle(scanner.nextLine());
            } else {
                state.setExitRequested(true);
            }
        }
        publish(GameEventType.INFO, "Kilépés.");
    }

    private void showAvailableCommands() {
        List<Command> available = registry.getAvailableCommands(state.getPhase());
        String names = available.stream().map(Command::getSyntax).collect(Collectors.joining(", "));
        publish(GameEventType.INFO, "Elérhető parancsok: " + names);
    }

    private void handle(String line) {
        String input = line.trim();
        if (input.isEmpty()) {
            return;
        }
        Optional<CommandRegistry.Match> match = registry.find(input, state.getPhase());
        if (match.isPresent()) {
            execute(match.get());
        } else {
            reportUnavailable(input);
        }
    }

    private void execute(CommandRegistry.Match match) {
        try {
            match.command().apply(state, match.matcher());
        } catch (IllegalArgumentException | IllegalStateException e) {
            publish(GameEventType.INVALID_MOVE, e.getMessage());
        } catch (RuntimeException e) {
            publish(GameEventType.ERROR, "Váratlan hiba: " + e.getMessage());
        }
    }

    private void reportUnavailable(String input) {
        if (registry.matchesInAnyPhase(input)) {
            publish(GameEventType.INVALID_COMMAND,
                    "A(z) " + input + " parancs most nem érhető el (" + state.getPhase() + ").");
        } else {
            publish(GameEventType.INVALID_COMMAND,
                    "Ismeretlen parancs: " + input + ". A parancsok listája: help");
        }
    }

    private void publish(GameEventType type, String message) {
        publisher.publish(new GameEvent(type, message));
    }
}
