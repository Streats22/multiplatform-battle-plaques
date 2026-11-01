package dev.battleplacks.config;

/**
 * How the party is drawn ("bar style" in the settings). Each constant has a stable network id, so the order of the
 * constants can change without breaking what is sent between players.
 * <ul>
 * <li>PIPS: Cobblemon's own tile bar with slanted pips (default)</li>
 * <li>SEGMENTED: a plain dark bar with straight segments</li>
 * <li>BALLS: small poke balls</li>
 * </ul>
 */
public enum BallStyle {
    PIPS(0), SEGMENTED(1), BALLS(2);

    private final int id;

    BallStyle(int id) { this.id = id; }

    public int id() { return id; }

    /** The style with this network id, or {@code fallback} when the id is unknown (a newer or hostile client). */
    public static BallStyle fromId(int id, BallStyle fallback) {
        for (BallStyle style : values()) {
            if (style.id == id) return style;
        }
        return fallback;
    }

    /** Case-insensitive name lookup (e.g. from a command or hand-edited file); {@code fallback} when unknown or null. */
    public static BallStyle parse(String name, BallStyle fallback) {
        if (name == null) return fallback;
        for (BallStyle style : values()) {
            if (style.name().equalsIgnoreCase(name.trim())) return style;
        }
        return fallback;
    }
}
