package businesslogic;

import java.util.ArrayList;
import java.util.List;
import model.Board;
import model.Direction;
import model.Move;
import model.Orientation;
import model.Position;
import model.Vehicle;

/**
 * A játékszabályok: lehetséges lépések, lépés végrehajtása, győzelem. A Board immutable, ezért minden
 * lépés új Boardot hoz létre, és a {@code board} mezőt cseréli le.
 */
public class BoardService {

    private Board board;
    private int moveCount = 0;

    public BoardService(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("A pálya nem lehet null.");
        }
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    /** Eddig megtett lépések száma (egy csúsztatás egy lépés, a távolságtól függetlenül). */
    public int getMoveCount() {
        return moveCount;
    }

    /** A piros autó elérte a jobb oldali kijáratot. */
    public boolean isSolved() {
        Vehicle red = board.getRedCar();
        return red.getPosition().getCol() + red.getLength() - 1 == board.getBoardSize() - 1;
    }

    /**
     * Minden szabályos csúsztatás: minden jármű a saját tengelye mentén, minden irányban 1, 2, ... mezőt,
     * amíg a pálya széle vagy másik jármű útját nem állja.
     */
    public List<Move> determinePossibleMoves() {
        List<Move> moves = new ArrayList<>();
        for (Vehicle vehicle : board.getVehicles()) {
            for (Direction direction : directionsOf(vehicle)) {
                int steps = 1;
                while (canSlide(vehicle, direction, steps)) {
                    moves.add(new Move(vehicle.getId(), direction, steps));
                    steps++;
                }
            }
        }
        return moves;
    }

    /**
     * Végrehajt egy lépést: új Board készül (a régi változatlan marad), és a {@code board} mező cserélődik.
     *
     * @throws IllegalArgumentException ha a lépés null, nincs ilyen jármű, vagy a lépés szabálytalan
     */
    public void doMove(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("A lépés nem lehet null.");
        }
        Vehicle vehicle = board.findVehicle(move.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen jármű: " + move.getVehicleId()));
        if (!determinePossibleMoves().contains(move)) {
            throw new IllegalArgumentException("Szabálytalan lépés: " + move);
        }
        board = board.withVehicle(vehicle.moved(move.getDirection(), move.getSteps()));
        moveCount++;
    }

    /** A pályán belül van-e a pozíció. (Csomag-szintű a teszteléshez.) */
    boolean fits(Position p) {
        int size = board.getBoardSize();
        return p.getRow() >= 0 && p.getRow() < size && p.getCol() >= 0 && p.getCol() < size;
    }

    /** A pályán belüli, és egyetlen jármű sem fedi. (Csomag-szintű a teszteléshez.) */
    boolean isFree(Position p) {
        if (!fits(p)) {
            return false;
        }
        for (Vehicle vehicle : board.getVehicles()) {
            if (vehicle.getCells().contains(p)) {
                return false;
            }
        }
        return true;
    }

    private static List<Direction> directionsOf(Vehicle vehicle) {
        return vehicle.getOrientation() == Orientation.HORIZONTAL
                ? List.of(Direction.LEFT, Direction.RIGHT)
                : List.of(Direction.UP, Direction.DOWN);
    }

    /** A teljes út szabad, ha a csúsztatás minden közbülső (kisebb) lépése is szabályos volt. */
    private boolean canSlide(Vehicle vehicle, Direction direction, int steps) {
        List<Position> ownCells = vehicle.getCells();
        for (Position cell : vehicle.moved(direction, steps).getCells()) {
            if (!isFree(cell) && !ownCells.contains(cell)) {
                return false;
            }
        }
        return true;
    }
}
