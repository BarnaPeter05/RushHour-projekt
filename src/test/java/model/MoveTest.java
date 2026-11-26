package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MoveTest {

    @Test
    void moveStoresItsValues() {
        Move move = new Move('B', Direction.DOWN, 2);

        assertEquals('B', move.getVehicleId());
        assertEquals(Direction.DOWN, move.getDirection());
        assertEquals(2, move.getSteps());
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Move('B', null, 1));
        assertThrows(IllegalArgumentException.class, () -> new Move('B', Direction.UP, 0));
        assertThrows(IllegalArgumentException.class, () -> new Move('B', Direction.UP, -2));
    }

    @Test
    void equalsAndHashCodeWork() {
        Move a = new Move('B', Direction.DOWN, 2);

        assertEquals(a, new Move('B', Direction.DOWN, 2));
        assertEquals(a.hashCode(), new Move('B', Direction.DOWN, 2).hashCode());
        assertNotEquals(a, new Move('B', Direction.DOWN, 1));
        assertNotEquals(a, new Move('B', Direction.UP, 2));
        assertNotEquals(a, new Move('C', Direction.DOWN, 2));
    }

    @Test
    void toStringIsReadable() {
        assertEquals("B DOWN 2", new Move('B', Direction.DOWN, 2).toString());
    }
}
