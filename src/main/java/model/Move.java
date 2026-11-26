package model;

import java.util.Objects;

/** Egy lépés: melyik jármű, melyik irányba, hány mezőt csúszik. A teljes csúsztatás egy lépés. */
public final class Move {

    private final char vehicleId;
    private final Direction direction;
    private final int steps;

    public Move(char vehicleId, Direction direction, int steps) {
        if (direction == null) {
            throw new IllegalArgumentException("Az irány nem lehet null.");
        }
        if (steps < 1) {
            throw new IllegalArgumentException("A lépésszám legalább 1 kell legyen: " + steps);
        }
        this.vehicleId = vehicleId;
        this.direction = direction;
        this.steps = steps;
    }

    public char getVehicleId() {
        return vehicleId;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSteps() {
        return steps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Move other = (Move) o;
        return vehicleId == other.vehicleId && direction == other.direction && steps == other.steps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, direction, steps);
    }

    @Override
    public String toString() {
        return vehicleId + " " + direction + " " + steps;
    }
}
