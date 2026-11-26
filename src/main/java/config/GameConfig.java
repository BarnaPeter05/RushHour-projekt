package config;

/** A játék beállításai. Singleton, a Sudoku GameConfig mintájára. */
public final class GameConfig {

    private static class Holder {
        private static final GameConfig INSTANCE = new GameConfig();
    }

    private final int boardSize = 6;
    private final int exitRow = 2;
    private final char redCarId = 'X';
    private final int leaderboardSize = 10;

    private GameConfig() {
    }

    public static GameConfig getInstance() {
        return Holder.INSTANCE;
    }

    /** A pálya oldalhossza (6x6). */
    public int getBoardSize() {
        return boardSize;
    }

    /** A kijárat sora a jobb oldalon (0-tól számolva). */
    public int getExitRow() {
        return exitRow;
    }

    /** A kijutó (piros) autó azonosítója a pályafájlokban. */
    public char getRedCarId() {
        return redCarId;
    }

    /** Hány eredményt mutat a ranglista pályánként. */
    public int getLeaderboardSize() {
        return leaderboardSize;
    }
}
