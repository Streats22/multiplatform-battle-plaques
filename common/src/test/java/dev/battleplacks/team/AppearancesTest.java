package dev.battleplacks.team;

import static org.junit.jupiter.api.Assertions.*;

import dev.battleplacks.config.BallStyle;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AppearancesTest {
    private final UUID player = UUID.randomUUID();

    @AfterEach
    void reset() { Appearances.clear(); }

    @Test
    void storesAndReturnsValidatedAppearance() {
        Appearances.set(player, "Team Rocket", 0xABCDEF, BallStyle.SEGMENTED.id());
        Appearance a = Appearances.get(player).orElseThrow();
        assertEquals("Team Rocket", a.name());
        assertEquals(0xABCDEF, a.accentRgb());
        assertEquals(BallStyle.SEGMENTED, a.style());
    }

    @Test
    void revalidatesWhateverTheServerSent() {
        Appearances.set(player, "f u c k", 0x7FFFFFFF, 42);
        Appearance a = Appearances.get(player).orElseThrow();
        assertEquals("", a.name());
        assertFalse(a.hasAccent());
        assertEquals(AppearanceRules.DEFAULT_STYLE, a.style());
    }

    @Test
    void unknownPlayerHasNoAppearanceSoTheThemeIsUsed() {
        assertTrue(Appearances.get(UUID.randomUUID()).isEmpty());
    }

    @Test
    void removeAndClearForgetPlayers() {
        Appearances.set(player, "A", 0, 0);
        Appearances.remove(player);
        assertTrue(Appearances.get(player).isEmpty());
        Appearances.set(player, "A", 0, 0);
        Appearances.clear();
        assertTrue(Appearances.get(player).isEmpty());
    }
}
