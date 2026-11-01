package dev.battleplacks.client.render;

import static org.junit.jupiter.api.Assertions.*;

import dev.battleplacks.config.PlaqueConfig;
import org.junit.jupiter.api.Test;

class StripLayoutTest {
    private final PlaqueConfig cfg = new PlaqueConfig();

    @Test
    void singlesAreLargerThanDoubles() {
        assertEquals(22f, StripLayout.pokemonSize(1, cfg), 0.001f);
        assertEquals(17f, StripLayout.pokemonSize(2, cfg), 0.001f);
    }

    @Test
    void sizePercentScalesThePortrait() {
        cfg.pokemonSize = 150;
        assertEquals(33f, StripLayout.pokemonSize(1, cfg), 0.001f);
    }

    @Test
    void defaultsMovePokemonUpByHalfItsHeightComparedToBefore() {
        float size = StripLayout.pokemonSize(1, cfg);
        float before = 100f + StripLayout.HEIGHT - size * 0.9f;   // the old formula
        float after = StripLayout.pokemonCentreY(100, size, cfg);
        assertEquals(size / 2f, before - after, 0.001f);
    }

    @Test
    void liftMovesUpAndNegativeLiftMovesDown() {
        cfg.pokemonLift = 0;
        float base = StripLayout.pokemonCentreY(0, 22f, cfg);
        cfg.pokemonLift = 10;
        assertEquals(base - 10f, StripLayout.pokemonCentreY(0, 22f, cfg), 0.001f);
        cfg.pokemonLift = -10;
        assertEquals(base + 10f, StripLayout.pokemonCentreY(0, 22f, cfg), 0.001f);
    }

    @Test
    void trainerDefaultsMatchTheOldLayout() {
        assertEquals(50f, StripLayout.trainerScale(cfg), 0.001f);
        assertEquals(100 + StripLayout.PAD, StripLayout.trainerTop(100, cfg));
        cfg.trainerSize = 120;
        cfg.trainerLift = 4;
        assertEquals(60f, StripLayout.trainerScale(cfg), 0.001f);
        assertEquals(100 + StripLayout.PAD - 4, StripLayout.trainerTop(100, cfg));
    }
}
