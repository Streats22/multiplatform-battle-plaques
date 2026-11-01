package dev.battleplacks.team;

import dev.battleplacks.config.BallStyle;

/**
 * The single place that decides what a valid {@link Appearance} is. The config, the server and every receiving
 * client call this, so they cannot disagree. Nothing here throws: bad input is replaced, never trusted.
 */
public final class AppearanceRules {
    public static final BallStyle DEFAULT_STYLE = BallStyle.PIPS;

    private AppearanceRules() {}

    /**
     * @param name      raw team name; a name that is too long or not allowed becomes "" (the default label)
     * @param accentRgb 0xRRGGBB; anything outside 0..0xFFFFFF (including -1) means "no accent, use the theme"
     * @param styleId   {@link BallStyle#id()}; an unknown id becomes {@link #DEFAULT_STYLE}
     */
    public static Appearance sanitize(String name, int accentRgb, int styleId) {
        TeamNameRules.Result result = TeamNameRules.validate(name);
        String cleanName = result.valid() ? result.name() : "";
        int accent = accentRgb >= 0 && accentRgb <= 0xFFFFFF ? accentRgb : Appearance.NO_ACCENT;
        return new Appearance(cleanName, accent, BallStyle.fromId(styleId, DEFAULT_STYLE));
    }

    /** Re-validates an existing appearance (e.g. before storing it). */
    public static Appearance sanitize(Appearance a) {
        return sanitize(a.name(), a.accentRgb(), a.style().id());
    }

    /** Converts a config colour (ARGB, 0 = unset) to the shared form. */
    public static int accentFromConfig(int argb) {
        return argb == 0 ? Appearance.NO_ACCENT : argb & 0xFFFFFF;
    }
}
