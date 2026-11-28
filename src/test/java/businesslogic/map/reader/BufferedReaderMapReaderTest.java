package businesslogic.map.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import businesslogic.exception.MapReaderException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;

class BufferedReaderMapReaderTest {

    @Test
    void readsEveryLine() throws MapReaderException {
        BufferedReaderMapReader reader = new BufferedReaderMapReader(
                new BufferedReader(new StringReader("a b\n\nc d\n")));

        assertEquals(List.of("a b", "", "c d"), reader.readMap());
    }

    @Test
    void emptyInputGivesEmptyList() throws MapReaderException {
        BufferedReaderMapReader reader = new BufferedReaderMapReader(new BufferedReader(new StringReader("")));

        assertEquals(List.of(), reader.readMap());
    }

    @Test
    void ioFailureIsWrappedIntoMapReaderException() {
        Reader failing = new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw new IOException("lemezhiba");
            }

            @Override
            public void close() {
            }
        };
        BufferedReaderMapReader reader = new BufferedReaderMapReader(new BufferedReader(failing));

        MapReaderException exception = assertThrows(MapReaderException.class, reader::readMap);

        assertEquals(true, exception.getMessage().contains("lemezhiba"));
    }
}
