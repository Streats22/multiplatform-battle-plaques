package dev.battleplacks.team;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProfanityFilterTest {
    @ParameterizedTest
    @ValueSource(strings = {"shit", "SHIT", "f u c k", "fuuuck", "sh1t", "$h!t", "Team shit", "f.u.c.k", "bitch", "kanker", "tyfus", "shït"})
    void blocksCommonSpellingsAndDisguises(String text) {
        assertTrue(ProfanityFilter.isProfane(text), text);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cock", "dick", "Big Dick Energy", "ass", "c0ck"})
    void blocksShortWordsAsWholeWords(String text) {
        assertTrue(ProfanityFilter.isProfane(text), text);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Team Rocket", "Ash", "Pikachu Fans", "Cumberland", "Assist", "Classic", "Titan", "Dickens", "Hello World", "Gym Leader Brock", ""})
    void allowsOrdinaryNames(String text) {
        assertFalse(ProfanityFilter.isProfane(text), text);
    }
}
