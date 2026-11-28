package businesslogic.map;

import businesslogic.exception.MapReaderException;
import businesslogic.map.reader.MapReaderInterface;
import java.util.List;

/** A betölthető pályák forrása: a pályák nevei és egy-egy pálya olvasója. */
public interface LevelSource {

    List<String> listLevelNames() throws MapReaderException;

    MapReaderInterface open(String levelName) throws MapReaderException;
}
