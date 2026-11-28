package command;

import java.util.EnumSet;
import java.util.regex.Matcher;
import model.GamePhase;
import model.GameState;

/** Ugyanazt a pályát elölről kezdi (a lépésszámláló nullázódik). */
public class Restart extends Command {

    private final LevelStarter starter;

    public Restart(LevelStarter starter) {
        super("restart", "restart", EnumSet.of(GamePhase.PLAYING, GamePhase.SOLVED));
        this.starter = starter;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        starter.start(state, state.getLevelName());
    }
}
