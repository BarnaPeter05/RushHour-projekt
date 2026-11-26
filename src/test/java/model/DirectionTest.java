package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DirectionTest {

    @Test
    void stepsPointTheRightWay() {
        assertEquals(-1, Direction.UP.getRowStep());
        assertEquals(0, Direction.UP.getColStep());
        assertEquals(1, Direction.DOWN.getRowStep());
        assertEquals(-1, Direction.LEFT.getColStep());
        assertEquals(1, Direction.RIGHT.getColStep());
        assertEquals(0, Direction.RIGHT.getRowStep());
    }

    @Test
    void horizontalDirectionsAreLeftAndRight() {
        assertTrue(Direction.LEFT.isHorizontal());
        assertTrue(Direction.RIGHT.isHorizontal());
        assertFalse(Direction.UP.isHorizontal());
        assertFalse(Direction.DOWN.isHorizontal());
    }

    @Test
    void oppositeIsItsOwnInverse() {
        for (Direction direction : Direction.values()) {
            assertEquals(direction, direction.opposite().opposite());
        }
        assertEquals(Direction.DOWN, Direction.UP.opposite());
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
    }
}
