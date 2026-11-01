package dev.battleplacks.team;

import dev.battleplacks.config.BallStyle;

/**
 * How a player wants their team card to look: team name ("" = default), accent colour and bar style. Always built
 * through {@link AppearanceRules}, so every instance is already validated and clamped.
 *
 * @param accentRgb 0xRRGGBB, or {@link #NO_ACCENT} to use the receiver's theme colour
 */
public record Appearance(String name, int accentRgb, BallStyle style) {
    public static final int NO_ACCENT = -1;

    public boolean hasAccent() { return accentRgb != NO_ACCENT; }

    /** The accent as opaque ARGB; only meaningful when {@link #hasAccent()}. */
    public int accentArgb() { return 0xFF000000 | accentRgb; }
}
