package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import model.GamePhase;
import org.junit.jupiter.api.Test;

class CommandRegistryTest {

    private static StubCommand playCommand() {
        return new StubCommand("play", "play (\\d+)", Set.of(GamePhase.PLAYING));
    }

    @Test
    void registeredCommandsKeepTheirOrder() {
        CommandRegistry registry = new CommandRegistry();
        Exit exit = new Exit();
        StubCommand play = playCommand();

        registry.register(exit);
        registry.register(play);

        assertEquals(List.of(exit, play), registry.getCommands());
    }

    @Test
    void returnedListCannotBeModified() {
        CommandRegistry registry = new CommandRegistry();
        List<Command> commands = registry.getCommands();

        assertThrows(UnsupportedOperationException.class, () -> commands.add(new Exit()));
    }

    @Test
    void nullAndDuplicateNamesAreRejected() {
        CommandRegistry registry = new CommandRegistry();
        registry.register(new Exit());

        assertThrows(IllegalArgumentException.class, () -> registry.register(null));
        assertThrows(IllegalArgumentException.class, () -> registry.register(new Exit()));
        assertThrows(IllegalArgumentException.class,
                () -> registry.register(new StubCommand("EXIT", "x", Set.of(GamePhase.MENU))));
    }

    @Test
    void availableCommandsDependOnThePhase() {
        CommandRegistry registry = new CommandRegistry();
        Exit exit = new Exit();
        StubCommand play = playCommand();
        registry.register(exit);
        registry.register(play);

        assertEquals(List.of(exit), registry.getAvailableCommands(GamePhase.MENU));
        assertEquals(List.of(exit, play), registry.getAvailableCommands(GamePhase.PLAYING));
    }

    @Test
    void findReturnsCommandAndMatchedArguments() {
        CommandRegistry registry = new CommandRegistry();
        StubCommand play = playCommand();
        registry.register(play);

        Optional<CommandRegistry.Match> match = registry.find("play 7", GamePhase.PLAYING);

        assertTrue(match.isPresent());
        assertSame(play, match.get().command());
        assertEquals("7", match.get().matcher().group(1));
    }

    @Test
    void findIgnoresCommandsOfOtherPhasesAndNonMatchingInput() {
        CommandRegistry registry = new CommandRegistry();
        registry.register(playCommand());

        assertTrue(registry.find("play 7", GamePhase.MENU).isEmpty());
        assertTrue(registry.find("play x", GamePhase.PLAYING).isEmpty());
        assertTrue(registry.find(null, GamePhase.PLAYING).isEmpty());
    }

    @Test
    void firstMatchingCommandWins() {
        CommandRegistry registry = new CommandRegistry();
        StubCommand first = new StubCommand("first", ".*", Set.of(GamePhase.MENU));
        StubCommand second = new StubCommand("second", ".*", Set.of(GamePhase.MENU));
        registry.register(first);
        registry.register(second);

        assertSame(first, registry.find("anything", GamePhase.MENU).orElseThrow().command());
    }

    @Test
    void matchesInAnyPhaseLooksAtSyntaxOnly() {
        CommandRegistry registry = new CommandRegistry();
        registry.register(playCommand());

        assertTrue(registry.matchesInAnyPhase("play 3"));
        assertFalse(registry.matchesInAnyPhase("jump"));
    }
}
