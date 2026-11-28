package businesslogic.map;

import businesslogic.exception.MapReaderException;
import businesslogic.map.reader.BufferedReaderMapReader;
import businesslogic.map.reader.MapReaderInterface;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A pályák a jar-ba csomagolt resources/map mappából jönnek. A betölthető pályák nevét a levels.txt
 * sorolja fel (jarból a mappa tartalma nem listázható), így csak a felsorolt nevek nyithatók meg.
 */
public class ClassPathLevelSource implements LevelSource {

    private static final String DIRECTORY = "map/";
    private static final String INDEX = DIRECTORY + "levels.txt";

    @Override
    public List<String> listLevelNames() throws MapReaderException {
        List<String> names = new ArrayList<>();
        for (String line : openResource(INDEX).readMap()) {
            String name = line.strip();
            if (!name.isEmpty() && !name.startsWith("#")) {
                names.add(name);
            }
        }
        return names;
    }

    @Override
    public MapReaderInterface open(String levelName) throws MapReaderException {
        if (!listLevelNames().contains(levelName)) {
            throw new MapReaderException("Nincs ilyen pálya: " + levelName);
        }
        return openResource(DIRECTORY + levelName + ".txt");
    }

    private MapReaderInterface openResource(String path) throws MapReaderException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new MapReaderException("Hiányzó erőforrás: " + path);
        }
        return new BufferedReaderMapReader(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
    }
}
