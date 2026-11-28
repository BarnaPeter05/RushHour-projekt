package businesslogic.map.reader;

import businesslogic.exception.MapReaderException;
import java.util.List;

/** Egy pályafájl nyers sorainak beolvasása. */
public interface MapReaderInterface {

    List<String> readMap() throws MapReaderException;
}
