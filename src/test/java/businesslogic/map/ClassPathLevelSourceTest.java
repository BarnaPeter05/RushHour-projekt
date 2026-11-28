package businesslogic.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import businesslogic.exception.MapReaderException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClassPathLevelSourceTest {

    private final ClassPathLevelSource source = new ClassPathLevelSource();

    @Test
    void listsTheBundledLevelsInOrder() throws MapReaderException {
        assertEquals(List.of("beginner", "easy", "medium", "hard", "expert"), source.listLevelNames());
    }

    @Test
    void opensEveryListedLevel() throws MapReaderException {
        for (String name : source.listLevelNames()) {
            assertFalse(source.open(name).readMap().isEmpty(), name);
        }
    }

    @Test
    void unlistedLevelsCannotBeOpened() {
        assertThrows(MapReaderException.class, () -> source.open("nincsilyen"));
        assertThrows(MapReaderException.class, () -> source.open("../logback"));
    }
}
