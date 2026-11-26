package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class BoardTest {

    private static Vehicle red() {
        return new Vehicle('X', new Position(2, 0), 2, Orientation.HORIZONTAL);
    }

    private static Vehicle blocker() {
        return new Vehicle('A', new Position(0, 3), 3, Orientation.VERTICAL);
    }

    private static Board.Builder valid() {
        return Board.builder().withBoardSize(6).addVehicle(red()).addVehicle(blocker());
    }

    @Test
    void gettersReturnBuilderValues() {
        Board board = valid().build();

        assertEquals(6, board.getBoardSize());
        assertEquals(List.of(red(), blocker()), board.getVehicles());
        assertEquals(red(), board.getRedCar());
        assertEquals(blocker(), board.findVehicle('A').orElseThrow());
        assertFalse(board.findVehicle('Z').isPresent());
    }

    @Test
    void returnedVehicleListCannotBeModified() {
        List<Vehicle> returned = valid().build().getVehicles();

        assertThrows(UnsupportedOperationException.class, () -> returned.add(red()));
    }

    @Test
    void externalListChangeDoesNotAffectBoard() {
        List<Vehicle> vehicles = new ArrayList<>(List.of(red(), blocker()));
        Board board = Board.builder().withBoardSize(6).withVehicles(vehicles).build();

        vehicles.clear();

        assertEquals(2, board.getVehicles().size());
    }

    @Test
    void overlappingVehiclesAreRejected() {
        Board.Builder builder = valid().addVehicle(new Vehicle('B', new Position(2, 1), 2, Orientation.HORIZONTAL));

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void vehicleOutsideTheBoardIsRejected() {
        Board.Builder right = valid().addVehicle(new Vehicle('B', new Position(5, 5), 2, Orientation.HORIZONTAL));
        Board.Builder left = valid().addVehicle(new Vehicle('C', new Position(4, -1), 2, Orientation.HORIZONTAL));
        Board.Builder bottom = valid().addVehicle(new Vehicle('D', new Position(4, 0), 3, Orientation.VERTICAL));

        assertThrows(IllegalStateException.class, right::build);
        assertThrows(IllegalStateException.class, left::build);
        assertThrows(IllegalStateException.class, bottom::build);
    }

    @Test
    void duplicateIdIsRejected() {
        Board.Builder builder = valid().addVehicle(new Vehicle('A', new Position(5, 0), 2, Orientation.HORIZONTAL));

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void redCarIsRequiredAndMustBeHorizontal() {
        Board.Builder noRed = Board.builder().withBoardSize(6).addVehicle(blocker());
        Board.Builder verticalRed = Board.builder().withBoardSize(6)
                .addVehicle(new Vehicle('X', new Position(0, 0), 2, Orientation.VERTICAL));

        assertThrows(IllegalStateException.class, noRed::build);
        assertThrows(IllegalStateException.class, verticalRed::build);
    }

    @Test
    void invalidSizeAndNullListAreRejected() {
        assertThrows(IllegalStateException.class, () -> valid().withBoardSize(0).build());
        assertThrows(IllegalStateException.class, () -> valid().withVehicles(null).build());
    }

    @Test
    void withVehicleReplacesAndKeepsTheOldBoard() {
        Board original = valid().build();

        Board updated = original.withVehicle(blocker().moved(Direction.DOWN, 2));

        assertEquals(new Position(2, 3), updated.findVehicle('A').orElseThrow().getPosition());
        assertEquals(new Position(0, 3), original.findVehicle('A').orElseThrow().getPosition());
        assertEquals(original.getRedCar(), updated.getRedCar());
    }

    @Test
    void withVehicleRejectsUnknownVehicle() {
        Board board = valid().build();
        Vehicle stranger = new Vehicle('Q', new Position(5, 0), 2, Orientation.HORIZONTAL);

        assertThrows(IllegalArgumentException.class, () -> board.withVehicle(stranger));
    }

    @Test
    void equalsAndHashCodeWork() {
        Board a = valid().build();
        Board b = valid().build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, valid().addVehicle(new Vehicle('B', new Position(5, 0), 2, Orientation.HORIZONTAL)).build());
    }

    @Test
    void toStringDrawsHeaderVehiclesAndExit() {
        String[] lines = valid().build().toString().split("\n");

        assertEquals(7, lines.length);
        assertEquals('X', lines[3].charAt(4));
        assertEquals('X', lines[3].charAt(6));
        assertEquals('A', lines[1].charAt(10));
        assertTrue(lines[3].endsWith(">"));
        assertFalse(lines[1].endsWith(">"));
    }
}