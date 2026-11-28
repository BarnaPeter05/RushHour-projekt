package businesslogic.map.reader;

import businesslogic.exception.MapReaderException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** BufferedReaderből olvassa be a sorokat, majd lezárja azt. */
public class BufferedReaderMapReader implements MapReaderInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedReaderMapReader.class);

    private final BufferedReader bufferedReader;

    public BufferedReaderMapReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    @Override
    public List<String> readMap() throws MapReaderException {
        List<String> result = new ArrayList<>();
        try (bufferedReader) {
            String line = bufferedReader.readLine();
            while (line != null) {
                result.add(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error("Pálya olvasási hiba.", e);
            throw new MapReaderException("Pálya olvasási hiba: " + e.getMessage(), e);
        }
        return result;
    }
}
