package businesslogic;

import businesslogic.exception.MapReaderException;
import businesslogic.exception.MapValidationException;
import businesslogic.map.ClassPathLevelSource;
import businesslogic.map.LevelSource;
import businesslogic.map.parser.MapParser;
import businesslogic.map.validation.ExitRowValidator;
import businesslogic.map.validation.MapValidatorInterface;
import businesslogic.map.validation.NotSolvedValidator;
import config.GameConfig;
import java.util.List;
import model.Board;

/** Pályák listázása és betöltése: beolvasás, értelmezés, majd az összes ellenőrzés lefuttatása. */
public class LevelService {

    private final LevelSource source;
    private final MapParser parser;
    private final List<MapValidatorInterface> validators;

    public LevelService(LevelSource source, MapParser parser, List<MapValidatorInterface> validators) {
        this.source = source;
        this.parser = parser;
        this.validators = List.copyOf(validators);
    }

    /** A jar-ban lévő pályákat használó, a GameConfig szerint beállított szolgáltatás. */
    public static LevelService createDefault() {
        GameConfig config = GameConfig.getInstance();
        return new LevelService(new ClassPathLevelSource(), new MapParser(config.getBoardSize()),
                List.of(new ExitRowValidator(config.getExitRow()), new NotSolvedValidator()));
    }

    public List<String> getLevelNames() throws MapReaderException {
        return source.listLevelNames();
    }

    /**
     * @throws MapReaderException    ha a pálya nem olvasható
     * @throws MapValidationException ha a pálya szabály szerint nem megfelelő
     * @throws businesslogic.exception.MapParserException ha a szöveg nem értelmezhető
     */
    public Board loadLevel(String levelName) throws MapReaderException, MapValidationException {
        List<String> rawMap = source.open(levelName).readMap();
        Board board = parser.parseMap(rawMap);
        for (MapValidatorInterface validator : validators) {
            validator.validate(board);
        }
        return board;
    }
}
