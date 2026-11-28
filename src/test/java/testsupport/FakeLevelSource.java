package testsupport;

import businesslogic.exception.MapReaderException;
import businesslogic.map.LevelSource;
import businesslogic.map.reader.MapReaderInterface;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Memóriában tartott pályákat adó forrás a tesztekhez. */
public class FakeLevelSource implements LevelSource {

    private final Map<String, List<String>> levels;

    public FakeLevelSource(Map<String, List<String>> levels) {
        this.levels = levels;
    }

    /** sample (jó), wrongrow és solved (hibás) pályák. */
    public static FakeLevelSource standard() {
        Map<String, List<String>> levels = new LinkedHashMap<>();
        levels.put("sample", SampleBoards.SAMPLE_LINES);
        levels.put("wrongrow", SampleBoards.WRONG_ROW_LINES);
        levels.put("solved", SampleBoards.ALREADY_SOLVED_LINES);
        return new FakeLevelSource(levels);
    }

    @Override
    public List<String> listLevelNames() {
        return new ArrayList<>(levels.keySet());
    }

    @Override
    public MapReaderInterface open(String levelName) throws MapReaderException {
        if (!levels.containsKey(levelName)) {
            throw new MapReaderException("Nincs ilyen pálya: " + levelName);
        }
        return () -> levels.get(levelName);
    }
}