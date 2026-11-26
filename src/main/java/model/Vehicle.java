package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Egy jármű a pályán: azonosító betű, bal felső mező, hossz (2 vagy 3) és irány.
 * Immutable: a mozgatás új példányt ad vissza.
 */
public final class Vehicle {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 3;

    private final char id;
    private final Position position;
    private final int length;
    private final Orientation orientation;

    public Vehicle(char id, Position position, int length, Orientation orientation) {
        if (!Character.isLetter(id)) {
            throw new IllegalArgumentException("A jármű azonosítója betű kell legyen: " + id);
        }
        if (position == null || orientation == null) {
            throw new IllegalArgumentException("A pozíció és az irány nem lehet null.");
        }
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("A jármű hossza 2 vagy 3 lehet: " + length);
        }
        this.id = id;
        this.position = position;
        this.length = length;
        this.orientation = orientation;
    }

    public char getId() {
        return id;
    }

    /** A jármű bal felső mezője. */
    public Position getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    /** A jármű által elfoglalt mezők, a bal felsőtől kezdve. */
    public List<Position> getCells() {
        List<Position> cells = new ArrayList<>();
        Direction along = orientation == Orientation.HORIZONTAL ? Direction.RIGHT : Direction.DOWN;
        for (int i = 0; i < length; i++) {
            cells.add(new Position(position, along, i));
        }
        return cells;
    }

    /**
     * Az eltolt jármű új példánya.
     *
     * @throws IllegalArgumentException ha az irány nem a jármű tengelyébe esik
     */
    public Vehicle moved(Direction direction, int steps) {
        if (direction.isHorizontal() != (orientation == Orientation.HORIZONTAL)) {
            throw new IllegalArgumentException("A jármű csak a saját tengelye mentén mozoghat: " + id);
        }
        return new Vehicle(id, new Position(position, direction, steps), length, orientation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vehicle other = (Vehicle) o;
        return id == other.id && length == other.length && orientation == other.orientation
                && position.equals(other.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, position, length, orientation);
    }

    @Override
    public String toString() {
        return id + position.toString() + " " + length + " " + orientation;
    }
}
