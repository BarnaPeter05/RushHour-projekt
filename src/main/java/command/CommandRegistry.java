package command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import model.GamePhase;

/** A regisztrált parancsok listája, és annak keresése, hogy a bemenet melyik parancsnak felel meg. */
public class CommandRegistry {

    /** Egy találat: a parancs és a hozzá tartozó, már illeszkedett Matcher. */
    public record Match(Command command, Matcher matcher) {
    }

    private final List<Command> commands = new ArrayList<>();

    /** @throws IllegalArgumentException ha a parancs null, vagy már van azonos nevű regisztrálva */
    public void register(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("A parancs nem lehet null.");
        }
        for (Command registered : commands) {
            if (registered.getName().equalsIgnoreCase(command.getName())) {
                throw new IllegalArgumentException("Már regisztrált parancs: " + command.getName());
            }
        }
        commands.add(command);
    }

    /** Az összes parancs a regisztráció sorrendjében (módosíthatatlan másolat). */
    public List<Command> getCommands() {
        return List.copyOf(commands);
    }

    /** Az adott fázisban elérhető parancsok. */
    public List<Command> getAvailableCommands(GamePhase phase) {
        List<Command> available = new ArrayList<>();
        for (Command command : commands) {
            if (command.isApplicableInPhase(phase)) {
                available.add(command);
            }
        }
        return available;
    }

    /** Az első olyan parancs, amelyre a bemenet illeszkedik ÉS elérhető az adott fázisban. */
    public Optional<Match> find(String input, GamePhase phase) {
        for (Command command : commands) {
            if (command.isApplicableInPhase(phase)) {
                Matcher matcher = command.match(input);
                if (matcher.matches()) {
                    return Optional.of(new Match(command, matcher));
                }
            }
        }
        return Optional.empty();
    }

    /** Illeszkedik-e a bemenet valamelyik parancsra, a fázistól függetlenül. */
    public boolean matchesInAnyPhase(String input) {
        for (Command command : commands) {
            if (command.match(input).matches()) {
                return true;
            }
        }
        return false;
    }
}
