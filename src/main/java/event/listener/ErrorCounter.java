package event.listener;

import event.GameEvent;
import event.GameEventListener;
import event.GameEventType;

public class ErrorCounter implements GameEventListener {

    private int errorCount = 0;

    @Override
    public void onEvent(GameEvent event) {
        GameEventType type = event.type();
        if (type == GameEventType.ERROR || type == GameEventType.INVALID_MOVE
                || type == GameEventType.INVALID_COMMAND) {
            errorCount++;
        }
    }

    public int getErrorCount() {
        return errorCount;
    }
}
