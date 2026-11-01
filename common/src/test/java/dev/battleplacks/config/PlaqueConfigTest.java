package dev.battleplacks.config;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import dev.battleplacks.team.Appearance;
import org.junit.jupiter.api.Test;

class PlaqueConfigTest {
    @Test
    void defaultsRaisePokemonByAboutHalfItsHeight() {
        PlaqueConfig c = new PlaqueConfig();
        assertEquals(100, c.pokemonSize);
        assertEquals(11, c.pokemonLift); // half of the 22 px base portrait
        assertEquals(100, c.trainerSize);
        assertEquals(0, c.trainerLift);
    }

    @Test
    void defaultLookIsTheCurrentOne() {
        PlaqueConfig c = new PlaqueConfig();
        assertEquals(BallStyle.PIPS, c.ballStyle);
        assertEquals(LabelPosition.BELOW, c.labelPosition);
        assertEquals("", c.teamName);
        assertEquals(0, c.accentOwn);
    }

    @Test
    void defaultAppearanceUsesThemeColour() {
        Appearance a = new PlaqueConfig().appearance();
        assertEquals("", a.name());
        assertFalse(a.hasAccent());
        assertEquals(BallStyle.PIPS, a.style());
    }

    @Test
    void appearanceFollowsTheConfig() {
        PlaqueConfig c = new PlaqueConfig();
        c.teamName = "  Team  Ash ";
        c.accentOwn = 0xFF3366CC;
        c.ballStyle = BallStyle.SEGMENTED;
        Appearance a = c.appearance();
        assertEquals("Team Ash", a.name());
        assertEquals(0x3366CC, a.accentRgb());
        assertEquals(BallStyle.SEGMENTED, a.style());
    }

    @Test
    void sanitizeClampsNumbers() {
        PlaqueConfig c = new PlaqueConfig();
        c.pokemonSize = 5000;
        c.trainerSize = -3;
        c.pokemonLift = 999;
        c.trainerLift = -999;
        c.sanitize();
        assertEquals(PlaqueConfig.MAX_SIZE, c.pokemonSize);
        assertEquals(PlaqueConfig.MIN_SIZE, c.trainerSize);
        assertEquals(PlaqueConfig.MAX_LIFT, c.pokemonLift);
        assertEquals(-PlaqueConfig.MAX_LIFT, c.trainerLift);
    }

    @Test
    void oldConfigFileWithoutNewFieldsGetsDefaults() {
        PlaqueConfig c = new Gson().fromJson("{\"teamName\":\"Old\",\"ballStyle\":\"BALLS\"}", PlaqueConfig.class).sanitize();
        assertEquals("Old", c.teamName);
        assertEquals(BallStyle.BALLS, c.ballStyle);
        assertEquals(LabelPosition.BELOW, c.labelPosition);
        assertEquals(11, c.pokemonLift);
    }

    @Test
    void unknownEnumValuesInTheFileAreRepaired() {
        PlaqueConfig c = new Gson().fromJson("{\"ballStyle\":\"HEXAGONS\",\"labelPosition\":\"SIDEWAYS\",\"theme\":\"NEON\"}", PlaqueConfig.class).sanitize();
        assertEquals(BallStyle.PIPS, c.ballStyle);
        assertEquals(LabelPosition.BELOW, c.labelPosition);
        assertEquals(Theme.COBBLEMON, c.theme);
    }
}
