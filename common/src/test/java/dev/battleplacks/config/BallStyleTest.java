package dev.battleplacks.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BallStyleTest {
    @Test
    void idsAreUniqueAndRoundTrip() {
        Set<Integer> seen = new HashSet<>();
        for (BallStyle style : BallStyle.values()) {
            assertTrue(seen.add(style.id()), "duplicate id " + style);
            assertEquals(style, BallStyle.fromId(style.id(), null));
        }
    }

    @Test
    void networkIdsAreStable() {
        assertEquals(0, BallStyle.PIPS.id());
        assertEquals(1, BallStyle.SEGMENTED.id());
        assertEquals(2, BallStyle.BALLS.id());
    }

    @Test
    void unknownIdUsesFallback() {
        assertEquals(BallStyle.PIPS, BallStyle.fromId(-1, BallStyle.PIPS));
        assertEquals(BallStyle.BALLS, BallStyle.fromId(77, BallStyle.BALLS));
    }

    @Test
    void parsesNamesCaseInsensitively() {
        assertEquals(BallStyle.SEGMENTED, BallStyle.parse("segmented", BallStyle.PIPS));
        assertEquals(BallStyle.BALLS, BallStyle.parse("  Balls ", BallStyle.PIPS));
        assertEquals(BallStyle.PIPS, BallStyle.parse("nope", BallStyle.PIPS));
        assertEquals(BallStyle.PIPS, BallStyle.parse(null, BallStyle.PIPS));
    }
}
