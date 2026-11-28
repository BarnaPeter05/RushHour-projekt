package businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import model.Board;
import model.Move;
import model.Vehicle;
import org.junit.jupiter.api.Test;

/** Minden mellékelt pálya megoldható, és a legrövidebb megoldás hossza a fájlban jelzett érték. */
class LevelSolvabilityTest {

    private static final Map<String, Integer> OPTIMUM = Map.of(
            "beginner", 2, "easy", 6, "medium", 10, "hard", 13, "expert", 21);

    /** Szélességi keresés: a legkevesebb lépés a megoldásig, vagy -1, ha nem megoldható. */
    private static int shortestSolution(Board start) {
        Map<Board, Integer> depth = new HashMap<>();
        Deque<Board> queue = new ArrayDeque<>();
        depth.put(start, 0);
        queue.add(start);
        while (!queue.isEmpty()) {
            Board current = queue.poll();
            BoardService service = new BoardService(current);
            if (service.isSolved()) {
                return depth.get(current);
            }
            for (Move move : service.determinePossibleMoves()) {
                Vehicle vehicle = current.findVehicle(move.getVehicleId()).orElseThrow();
                Board next = current.withVehicle(vehicle.moved(move.getDirection(), move.getSteps()));
                if (!depth.containsKey(next)) {
                    depth.put(next, depth.get(current) + 1);
                    queue.add(next);
                }
            }
        }
        return -1;
    }

    @Test
    void everyBundledLevelIsSolvableInTheRecordedNumberOfMoves() throws Exception {
        LevelService levels = LevelService.createDefault();

        assertEquals(OPTIMUM.keySet(), Set.copyOf(levels.getLevelNames()));
        for (String name : levels.getLevelNames()) {
            assertEquals(OPTIMUM.get(name).intValue(), shortestSolution(levels.loadLevel(name)), name);
        }
    }
}

