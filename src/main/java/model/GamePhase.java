package model;

/** A játék életciklusának fázisai. A parancsok ezek alapján dönthetik el, hogy elérhetők-e. */
public enum GamePhase {

    /** Menü: még nincs betöltött pálya (játékosnév megadása, pályaválasztás, ranglista). */
    MENU,

    /** Betöltött pálya, a játék folyamatban van. */
    PLAYING,

    /** A piros autó kijutott: a játék megoldva, az eredmény rögzítve. */
    SOLVED
}