package command;

import businesslogic.BoardService;
import businesslogic.LevelService;
import businesslogic.exception.MapParserException;
import businesslogic.exception.MapReaderException;
import businesslogic.exception.MapValidationException;
import event.GameEvent;
import event.GameEventPublisher;
import event.GameEventType;
import model.Board;
import model.GamePhase;
import model.GameState;

/** Közös logika a pálya (újra)indításához: a load és a restart parancs is ezt használja. */
public class LevelStarter {

    private final LevelService levelService;
    private final GameEventPublisher publisher;

    public LevelStarter(LevelService levelService, GameEventPublisher publisher) {
        this.levelService = levelService;
        this.publisher = publisher;
    }

    /** @throws IllegalArgumentException ha a pálya nem tölthető be */
    public void start(GameState state, String levelName) {
        Board board;
        try {
            board = levelService.loadLevel(levelName);
        } catch (MapReaderException | MapValidationException | MapParserException e) {
            throw new IllegalArgumentException("A(z) " + levelName + " pálya nem tölthető be: " + e.getMessage(), e);
        }
        state.setBoardService(new BoardService(board));
        state.setLevelName(levelName);
        state.setPhase(GamePhase.PLAYING);
        publisher.publish(new GameEvent(GameEventType.INFO, "Pálya betöltve: " + levelName + "\n" + board));
    }
}
