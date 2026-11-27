package event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import event.listener.ErrorCounter;
import org.junit.jupiter.api.Test;

class ErrorCounterTest {

    @Test
    void countsErrorsInvalidMovesAndInvalidCommands() {
        ErrorCounter counter = new ErrorCounter();

        counter.onEvent(new GameEvent(GameEventType.MOVE_DONE, "ok"));
        counter.onEvent(new GameEvent(GameEventType.INFO, "info"));
        counter.onEvent(new GameEvent(GameEventType.INVALID_MOVE, "rossz lépés"));
        counter.onEvent(new GameEvent(GameEventType.INVALID_COMMAND, "rossz parancs"));
        counter.onEvent(new GameEvent(GameEventType.ERROR, "hiba"));

        assertEquals(3, counter.getErrorCount());
    }
}
