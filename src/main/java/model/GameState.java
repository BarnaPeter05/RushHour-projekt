package model;

import businesslogic.BoardService;

/**
 * A játékmenet változó (mutable) állapota. A parancsok ezt módosítják, a GameEngine pedig ez alapján
 * dönti el, melyik parancs érhető el.
 */
public class GameState {

    private GamePhase phase = GamePhase.MENU;
    private String playerName = null;
    private String levelName = null;
    private BoardService boardService;
    private boolean exitRequested = false;

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    /** A játékos neve; null, amíg nem adta meg. */
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /** A betöltött pálya neve; null, amíg nincs betöltött pálya. */
    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public BoardService getBoardService() {
        return boardService;
    }

    public void setBoardService(BoardService boardService) {
        this.boardService = boardService;
    }

    public boolean isExitRequested() {
        return exitRequested;
    }

    public void setExitRequested(boolean exitRequested) {
        this.exitRequested = exitRequested;
    }
}
