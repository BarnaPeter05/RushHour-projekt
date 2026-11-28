package businesslogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import businesslogic.exception.MapParserException;
import businesslogic.exception.MapReaderException;
import businesslogic.exception.MapValidationException;
import businesslogic.map.parser.MapParser;
import businesslogic.map.validation.ExitRowValidator;
import businesslogic.map.validation.NotSolvedValidator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Board;
import org.junit.jupiter.api.Test;
import testsupport.FakeLevelSource;
import testsupport.SampleBoards;

class LevelServiceTest {

    private final LevelService service = new LevelService(FakeLevelSource.standard(), new MapParser(6),
            List.of(new ExitRowValidator(2), new NotSolvedValidator()));

    @Test
    void listsTheLevelNames() throws MapReaderException {
        assertEquals(List.of("sample", "wrongrow", "solved"), service.getLevelNames());
    }

    @Test
    void loadsAValidLevel() throws Exception {
        Board board = service.loadLevel("sample");

        assertEquals(5, board.getVehicles().size());
    }

    @Test
    void unknownLevelIsAReaderError() {
        assertThrows(MapReaderException.class, () -> service.loadLevel("nincsilyen"));
    }

    @Test
    void levelBreakingARuleIsAValidationError() {
        assertThrows(MapValidationException.class, () -> service.loadLevel("wrongrow"));
        assertThrows(MapValidationException.class, () -> service.loadLevel("solved"));
    }

    @Test
    void unparsableLevelIsAParserError() {
        Map<String, List<String>> levels = new LinkedHashMap<>();
        levels.put("short", List.of("X X . . . ."));
        LevelService broken = new LevelService(new FakeLevelSource(levels), new MapParser(6), List.of());

        assertThrows(MapParserException.class, () -> broken.loadLevel("short"));
    }

    @Test
    void defaultServiceLoadsTheBundledLevels() throws Exception {
        LevelService bundled = LevelService.createDefault();

        assertEquals(5, bundled.getLevelNames().size());
        assertEquals(6, bundled.loadLevel("beginner").getBoardSize());
    }

    @Test
    void sampleLinesConstantsMatchTheSampleBoard() throws Exception {
        Board loaded = service.loadLevel("sample");

        assertEquals(SampleBoards.sampleBoard().getVehicles().size(), loaded.getVehicles().size());
    }
}

