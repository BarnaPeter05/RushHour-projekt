package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    void constructorStoresRowAndCol() {
        Position p = new Position(2, 5);

        assertEquals(2, p.getRow());
        assertEquals(5, p.getCol());
    }

    @Test
    void stepInEveryDirection() {
        Position c = new Position(3, 3);

        assertEquals(new Position(2, 3), new Position(c, Direction.UP));
        assertEquals(new Position(4, 3), new Position(c, Direction.DOWN));
        assertEquals(new Position(3, 2), new Position(c, Direction.LEFT));
        assertEquals(new Position(3, 4), new Position(c, Direction.RIGHT));
    }
    @Test
    void stepWithMultipleSteps() {
        assertEquals(new Position(2, 4), new Position(new Position(2, 1), Direction.RIGHT, 3));
        assertEquals(new Position(5, 3), new Position(new Position(2, 3), Direction.DOWN, 3));
        assertEquals(new Position(2, 1), new Position(new Position(2, 1), Direction.LEFT, 0));
    }

    @Test
    void equalsAndHashCodeWork() {
        Position a = new Position(1, 2);
        Position b = new Position(1, 2);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Position(2, 1));
    }

    @Test
    void toStringIsReadable() {
        assertEquals("(1,2)", new Position(1, 2).toString());
    }
}
