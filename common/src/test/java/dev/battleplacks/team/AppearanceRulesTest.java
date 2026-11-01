package dev.battleplacks.team;

import static org.junit.jupiter.api.Assertions.*;

import dev.battleplacks.config.BallStyle;
import org.junit.jupiter.api.Test;

class AppearanceRulesTest {
    @Test
    void validInputPassesThrough() {
        Appearance a = AppearanceRules.sanitize("Team Rocket", 0xFF8800, BallStyle.SEGMENTED.id());
        assertEquals("Team Rocket", a.name());
        assertEquals(0xFF8800, a.accentRgb());
        assertTrue(a.hasAccent());
        assertEquals(0xFFFF8800, a.accentArgb());
        assertEquals(BallStyle.SEGMENTED, a.style());
    }

    @Test
    void blackIsARealAccentNotAMissingOne() {
        Appearance a = AppearanceRules.sanitize("", 0x000000, 0);
        assertTrue(a.hasAccent());
        assertEquals(0xFF000000, a.accentArgb());
    }

    @Test
    void outOfRangeAccentMeansTheme() {
        for (int bad : new int[]{-1, -5, 0x1000000, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            assertFalse(AppearanceRules.sanitize("", bad, 0).hasAccent(), "accent " + bad);
        }
    }

    @Test
    void unknownStyleFallsBackToDefault() {
        for (int bad : new int[]{-1, 99, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            assertEquals(AppearanceRules.DEFAULT_STYLE, AppearanceRules.sanitize("", 0, bad).style(), "style " + bad);
        }
    }

    @Test
    void profaneOrOverlongNameBecomesDefaultButColourAndStyleSurvive() {
        Appearance profane = AppearanceRules.sanitize("sh1t", 0x112233, BallStyle.BALLS.id());
        assertEquals("", profane.name());
        assertEquals(0x112233, profane.accentRgb());
        assertEquals(BallStyle.BALLS, profane.style());

        assertEquals("", AppearanceRules.sanitize("x".repeat(500), 0, 0).name());
        assertEquals("", AppearanceRules.sanitize(null, 0, 0).name());
    }

    @Test
    void nameIsCleaned() {
        assertEquals("Team Ash", AppearanceRules.sanitize("  Team \u0007  Ash ", 0, 0).name());
    }

    @Test
    void sanitizingTwiceChangesNothing() {
        Appearance once = AppearanceRules.sanitize("  Team  A ", 0x123456, 1);
        assertEquals(once, AppearanceRules.sanitize(once));
    }

    @Test
    void configColourConversion() {
        assertEquals(Appearance.NO_ACCENT, AppearanceRules.accentFromConfig(0));
        assertEquals(0x00FF00, AppearanceRules.accentFromConfig(0xFF00FF00));
        // an alpha of 0 with a colour set still counts as that colour
        assertEquals(0x0000FF, AppearanceRules.accentFromConfig(0x000000FF));
    }
}
