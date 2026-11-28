package command;

import java.util.EnumSet;
import java.util.Locale;
import java.util.regex.Matcher;
import model.GamePhase;
import model.GameState;

/** Betölt egy pályát, és elindítja a játékot (MENU vagy SOLVED fázisból PLAYING-be). */
public class LoadLevel extends Command {

    private final LevelStarter starter;

    public LoadLevel(LevelStarter starter) {
        super("load", "load\\s+([\\w-]+)", EnumSet.of(GamePhase.MENU, GamePhase.SOLVED));
        this.starter = starter;
    }

    @Override
    public String getSyntax() {
        return "load <pálya>";
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        if (state.getPlayerName() == null) {
            throw new IllegalArgumentException("Előbb add meg a neved: name <játékosnév>");
        }
        starter.start(state, matcher.group(1).toLowerCase(Locale.ROOT));
    }
}
