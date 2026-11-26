package model;

import config.GameConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A pálya változtathatatlan (immutable) pillanatképe: méret és járművek. Felépítése a Sudoku MapVO-t követi:
 * privát konstruktor, Builder és "with..." metódus az új példány előállításához.
 * A Builder ellenőrzi a szerkezeti szabályokat: a járművek a pályán belül vannak, nem ütköznek,
 * az azonosítók egyediek, és pontosan egy vízszintes piros autó van.
 */
public final class Board {

    private final int boardSize;
    private final List<Vehicle> vehicles;

    private Board(Builder builder) {
        this.boardSize = builder.boardSize;
        this.vehicles = List.copyOf(builder.vehicles);
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getBoardSize() {
        return boardSize;
    }

    /** Módosíthatatlan lista, a sorrend a felvétel sorrendje. */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public Optional<Vehicle> findVehicle(char id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId() == id) {
                return Optional.of(vehicle);
            }
        }
        return Optional.empty();
    }

    /** A kijutó (piros) autó. A Builder garantálja, hogy van ilyen. */
    public Vehicle getRedCar() {
        return findVehicle(GameConfig.getInstance().getRedCarId()).orElseThrow();
    }

    /** Új Board, amelyben az azonos azonosítójú jármű le van cserélve. A régi Board változatlan marad. */
    public Board withVehicle(Vehicle replacement) {
        List<Vehicle> updated = new ArrayList<>();
        boolean replaced = false;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId() == replacement.getId()) {
                updated.add(replacement);
                replaced = true;
            } else {
                updated.add(vehicle);
            }
        }
        if (!replaced) {
            throw new IllegalArgumentException("Nincs ilyen jármű: " + replacement.getId());
        }
        return builder().withBoardSize(boardSize).withVehicles(updated).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Board board = (Board) o;
        return boardSize == board.boardSize && vehicles.equals(board.vehicles);
    }

    @Override
    public int hashCode() {
        return 31 * boardSize + vehicles.hashCode();
    }

    /** Szöveges rajz: a járművek az azonosítójukkal, az üres mező ponttal jelenik meg, a kijárat a &gt; jel. */
    @Override
    public String toString() {
        char[][] grid = new char[boardSize][boardSize];
        for (char[] row : grid) {
            Arrays.fill(row, '.');
        }
        for (Vehicle vehicle : vehicles) {
            for (Position cell : vehicle.getCells()) {
                grid[cell.getRow()][cell.getCol()] = vehicle.getId();
            }
        }
        int exitRow = getRedCar().getPosition().getRow();
        StringBuilder sb = new StringBuilder("   ");
        for (int col = 0; col < boardSize; col++) {
            sb.append(String.format("%2d", col));
        }
        for (int row = 0; row < boardSize; row++) {
            StringBuilder line = new StringBuilder(String.format("%2d ", row));
            for (int col = 0; col < boardSize; col++) {
                line.append(' ').append(grid[row][col]);
            }
            if (row == exitRow) {
                line.append(" >");
            }
            sb.append('\n').append(line);
        }
        return sb.toString();
    }

    public static final class Builder {

        private int boardSize;
        private List<Vehicle> vehicles = new ArrayList<>();

        public Builder withBoardSize(int boardSize) {
            this.boardSize = boardSize;
            return this;
        }

        public Builder withVehicles(List<Vehicle> vehicles) {
            this.vehicles = vehicles == null ? null : new ArrayList<>(vehicles);
            return this;
        }

        public Builder addVehicle(Vehicle vehicle) {
            if (vehicles == null) {
                vehicles = new ArrayList<>();
            }
            vehicles.add(vehicle);
            return this;
        }

        /** @throws IllegalStateException ha a pálya szerkezete szabálytalan */
        public Board build() {
            if (boardSize <= 0) {
                throw new IllegalStateException("A pályaméret pozitív kell legyen.");
            }
            if (vehicles == null) {
                throw new IllegalStateException("A járművek listája nem lehet null.");
            }
            validateVehicles();
            return new Board(this);
        }

        private void validateVehicles() {
            char redId = GameConfig.getInstance().getRedCarId();
            Set<Character> ids = new HashSet<>();
            Set<Position> occupied = new HashSet<>();
            Vehicle red = null;
            for (Vehicle vehicle : vehicles) {
                if (!ids.add(vehicle.getId())) {
                    throw new IllegalStateException("Kétszer szereplő azonosító: " + vehicle.getId());
                }
                for (Position cell : vehicle.getCells()) {
                    if (cell.getRow() < 0 || cell.getRow() >= boardSize
                            || cell.getCol() < 0 || cell.getCol() >= boardSize) {
                        throw new IllegalStateException("A jármű kilóg a pályáról: " + vehicle);
                    }
                    if (!occupied.add(cell)) {
                        throw new IllegalStateException("Ütköző járművek a(z) " + cell + " mezőn.");
                    }
                }
                if (vehicle.getId() == redId) {
                    red = vehicle;
                }
            }
            if (red == null) {
                throw new IllegalStateException("Nincs piros autó (" + redId + ").");
            }
            if (red.getOrientation() != Orientation.HORIZONTAL) {
                throw new IllegalStateException("A piros autónak vízszintesnek kell lennie.");
            }
        }
    }
}
