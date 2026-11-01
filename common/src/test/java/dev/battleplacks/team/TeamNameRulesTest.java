package dev.battleplacks.team;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TeamNameRulesTest {
    @Test
    void emptyAndNullMeanDefault() {
        assertTrue(TeamNameRules.validate("").valid());
        assertEquals("", TeamNameRules.validate("").name());
        assertTrue(TeamNameRules.validate(null).valid());
        assertEquals("", TeamNameRules.validate("   ").name());
    }

    @Test
    void cleansWhitespaceControlAndSectionSigns() {
        assertEquals("Team Ash", TeamNameRules.clean("  Team \t  Ash\n"));
        String section = String.valueOf((char) 0xA7);
        // only the section sign is dropped, so a colour code can never take effect (the letter stays as plain text)
        assertEquals("cRed", TeamNameRules.clean(section + "cRed"));
        assertFalse(TeamNameRules.clean(section + "cRed" + section + "r").contains(section));
        assertEquals("AB", TeamNameRules.clean("A\u0007B"));
    }

    @Test
    void acceptsExactlyMaxLengthAndRejectsLonger() {
        assertTrue(TeamNameRules.validate("a".repeat(TeamNameRules.MAX_LENGTH)).valid());
        TeamNameRules.Result tooLong = TeamNameRules.validate("a".repeat(TeamNameRules.MAX_LENGTH + 1));
        assertFalse(tooLong.valid());
        assertFalse(tooLong.reason().isEmpty());
    }

    @Test
    void rejectsProfanityAndReturnsNoName() {
        TeamNameRules.Result result = TeamNameRules.validate("f u c k");
        assertFalse(result.valid());
        assertEquals("", result.name());
    }

    @Test
    void validNameIsReturnedCleaned() {
        assertEquals("Team Rocket", TeamNameRules.validate("  Team   Rocket ").name());
    }
}
