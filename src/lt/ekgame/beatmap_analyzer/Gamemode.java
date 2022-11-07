package lt.ekgame.beatmap_analyzer;

public enum Gamemode {

    UNKNOWN(-1), OSU(0), TAIKO(1), CATCH(2), MANIA(3);

    int numValue;

    Gamemode(int numValue) {
        this.numValue = numValue;
    }

    public static Gamemode fromInt(int value) {
        for (Gamemode gamemode : Gamemode.values())
            if (gamemode.numValue == value)
                return gamemode;
        return UNKNOWN;
    }
}
