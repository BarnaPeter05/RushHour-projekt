# Rush Hour

Parancssoros Rush Hour (Zsúfolt csúcsforgalom) játék, Java 21 és Maven. A build- és kódstílus-beállítások a
Sudoku mintából származnak, a csomagszerkezet saját.

## Szabályok

- 6x6-os pálya, benne 2 vagy 3 mező hosszú járművek, vízszintesen vagy függőlegesen.
- A jármű csak a saját tengelye mentén csúszhat, más járművön nem mehet át.
- A piros autó (`X`) a 2. sorban áll. A cél: kivinni a pálya jobb oldali kijáratán.
- Egy lépés = egy jármű csúsztatása, tetszőleges számú mezővel. Kevesebb lépés = jobb helyezés.

## Indítás

```
mvn clean verify
java -jar target/rush-hour-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Szerkezet (`src/main/java`)

| Csomag | Tartalom |
|---|---|
| (gyökér) `Main` | belépési pont |
| `model` | `Position`, `Direction`, `Vehicle`, `Board`, `Move`, `GamePhase`, `GameState` |
| `config` | `GameConfig` (Singleton) |
| `event`, `event.listener` | Observer: `GameEventPublisher`, listenerek |
| `businesslogic` | `BoardService` (szabályok), pályafájl reader/parser/validator |
| `command` | `Command`, `CommandRegistry`, `GameEngine` és a parancsok |
| `persistence` | `DbInit`, eredmények és ranglista |

A JaCoCo kizárásai: `Main`, `model`, `config`, `event`. A 80%-ot a `businesslogic`, a `command` és a
`persistence` tesztjeinek kell hozniuk.
