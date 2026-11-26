package model;

/**
 * Lépésirányok. A koordináták mátrixszerűek: a (0,0) bal felül van,
 * a sor lefelé, az oszlop jobbra nő.
 */
public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowStep;
    private final int colStep;

    Direction(int rowStep, int colStep) {
        this.rowStep = rowStep;
        this.colStep = colStep;
    }

    public int getRowStep() {
        return rowStep;
    }

    public int getColStep() {
        return colStep;
    }

    /** Vízszintes irány (LEFT vagy RIGHT). */
    public boolean isHorizontal() {
        return rowStep == 0;
    }

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
