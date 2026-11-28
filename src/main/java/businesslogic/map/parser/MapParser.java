package businesslogic.map.parser;

import businesslogic.exception.MapParserException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Board;
import model.Orientation;
import model.Position;
import model.Vehicle;

/**
 * A pályafájl sorait Boarddá alakítja. A formátum: size x size jel, a betű egy jármű mezője,
 * a pont az üres mező. A szóközök, az üres sorok és a # kezdetű megjegyzés-sorok nem számítanak.
 */
public class MapParser {

    private static final char EMPTY = '.';

    private final int size;

    public MapParser(int size) {
        this.size = size;
    }

    /** @throws MapParserException ha a szöveg nem értelmezhető pályaként */
    public Board parseMap(List<String> rawMap) {
        List<String> rows = cleanRows(rawMap);
        if (rows.size() != size) {
            throw new MapParserException("A pálya " + size + " sorból áll, de " + rows.size() + " sor van.");
        }
        Map<Character, List<Position>> cellsById = new LinkedHashMap<>();
        for (int row = 0; row < size; row++) {
            String line = rows.get(row);
            if (line.length() != size) {
                throw new MapParserException("A(z) " + row + ". sor " + line.length()
                        + " mezőből áll, de " + size + " kell.");
            }
            for (int col = 0; col < size; col++) {
                char symbol = line.charAt(col);
                if (symbol != EMPTY) {
                    if (!Character.isLetter(symbol)) {
                        throw new MapParserException("Érvénytelen jel a(z) " + new Position(row, col)
                                + " mezőn: " + symbol);
                    }
                    cellsById.computeIfAbsent(symbol, key -> new ArrayList<>()).add(new Position(row, col));
                }
            }
        }
        Board.Builder builder = Board.builder().withBoardSize(size);
        for (Map.Entry<Character, List<Position>> entry : cellsById.entrySet()) {
            builder.addVehicle(toVehicle(entry.getKey(), entry.getValue()));
        }
        try {
            return builder.build();
        } catch (IllegalStateException e) {
            throw new MapParserException(e.getMessage());
        }
    }

    private static List<String> cleanRows(List<String> rawMap) {
        List<String> rows = new ArrayList<>();
        for (String line : rawMap) {
            String stripped = line.strip();
            if (!stripped.isEmpty() && !stripped.startsWith("#")) {
                rows.add(stripped.replaceAll("\\s+", ""));
            }
        }
        return rows;
    }

    /** A mezők sor-folytonos beolvasási sorrendben érkeznek, így az első a bal felső mező. */
    private static Vehicle toVehicle(char id, List<Position> cells) {
        if (cells.size() < 2 || cells.size() > 3) {
            throw new MapParserException("A(z) " + id + " jármű " + cells.size()
                    + " mezőből áll, de 2 vagy 3 kell.");
        }
        Position first = cells.get(0);
        boolean horizontal = true;
        boolean vertical = true;
        for (int i = 0; i < cells.size(); i++) {
            horizontal &= cells.get(i).equals(new Position(first.getRow(), first.getCol() + i));
            vertical &= cells.get(i).equals(new Position(first.getRow() + i, first.getCol()));
        }
        if (horizontal) {
            return new Vehicle(id, first, cells.size(), Orientation.HORIZONTAL);
        }
        if (vertical) {
            return new Vehicle(id, first, cells.size(), Orientation.VERTICAL);
        }
        throw new MapParserException("A(z) " + id + " jármű mezői nem alkotnak egyenes, összefüggő szakaszt.");
    }
}