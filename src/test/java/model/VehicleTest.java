package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void gettersReturnConstructorValues() {
        Vehicle vehicle = new Vehicle('A', new Position(1, 2), 3, Orientation.VERTICAL);

        assertEquals('A', vehicle.getId());
        assertEquals(new Position(1, 2), vehicle.getPosition());
        assertEquals(3, vehicle.getLength());
        assertEquals(Orientation.VERTICAL, vehicle.getOrientation());
    }

    @Test
    void cellsOfHorizontalAndVerticalVehicles() {
        Vehicle horizontal = new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL);
        Vehicle vertical = new Vehicle('B', new Position(1, 3), 3, Orientation.VERTICAL);

        assertEquals(List.of(new Position(2, 0), new Position(2, 1)), horizontal.getCells());
        assertEquals(List.of(new Position(1, 3), new Position(2, 3), new Position(3, 3)), vertical.getCells());
    }

    @Test
    void movedReturnsNewVehicleAndKeepsTheOldOne() {
        Vehicle original = new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL);

        Vehicle moved = original.moved(Direction.RIGHT, 3);

        assertEquals(new Position(2, 3), moved.getPosition());
        assertEquals(new Position(2, 0), original.getPosition());
        assertEquals(original.getLength(), moved.getLength());
        assertEquals(original.getOrientation(), moved.getOrientation());
    }

    @Test
    void verticalVehicleMovesUpAndDown() {
        Vehicle vehicle = new Vehicle('B', new Position(2, 3), 3, Orientation.VERTICAL);

        assertEquals(new Position(1, 3), vehicle.moved(Direction.UP, 1).getPosition());
        assertEquals(new Position(4, 3), vehicle.moved(Direction.DOWN, 2).getPosition());
    }

    @Test
    void vehicleCannotLeaveItsAxis() {
        Vehicle horizontal = new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL);
        Vehicle vertical = new Vehicle('B', new Position(1, 3), 3, Orientation.VERTICAL);

        assertThrows(IllegalArgumentException.class, () -> horizontal.moved(Direction.UP, 1));
        assertThrows(IllegalArgumentException.class, () -> vertical.moved(Direction.LEFT, 1));
    }

    @Test
    void invalidArgumentsAreRejected() {
        Position p = new Position(0, 0);

        assertThrows(IllegalArgumentException.class, () -> new Vehicle('1', p, 2, Orientation.HORIZONTAL));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('A', p, 1, Orientation.HORIZONTAL));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('A', p, 4, Orientation.HORIZONTAL));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('A', null, 2, Orientation.HORIZONTAL));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('A', p, 2, null));
    }

    @Test
    void equalsAndHashCodeWork() {
        Vehicle a = new Vehicle('A', new Position(0, 0), 2, Orientation.HORIZONTAL);
        Vehicle b = new Vehicle('A', new Position(0, 0), 2, Orientation.HORIZONTAL);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Vehicle('A', new Position(0, 1), 2, Orientation.HORIZONTAL));
        assertNotEquals(a, new Vehicle('B', new Position(0, 0), 2, Orientation.HORIZONTAL));
    }

    @Test
    void toStringIsReadable() {
        assertEquals("A(1,2) 3 VERTICAL", new Vehicle('A', new Position(1, 2), 3, Orientation.VERTICAL).toString());
    }
}
