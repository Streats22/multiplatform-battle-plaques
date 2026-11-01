package dev.battleplacks.client.render;

import dev.battleplacks.config.PlaqueConfig;

/**
 * The strip's geometry as plain numbers (no Minecraft types), so it can be unit-tested. "Up" offsets raise the
 * model; percentages come from the config and are applied on top of the base sizes.
 */
final class StripLayout {
    static final int HEIGHT = 44, PAD = 3, TRAINER_X = 28, POKEMON_X = 44;
    private static final float TRAINER_BASE_SCALE = 50f;
    private static final float POKEMON_BASE = 22f, POKEMON_BASE_DOUBLES = 17f;

    private StripLayout() {}

    /** Edge of a Pokemon's square portrait; about a Cobblemon tile portrait at 100%. */
    static float pokemonSize(int activeCount, PlaqueConfig cfg) {
        return (activeCount > 1 ? POKEMON_BASE_DOUBLES : POKEMON_BASE) * cfg.pokemonSize / 100f;
    }

    /** Centre line of the portrait: the bottom of the strip minus most of its size, then raised by the offset. */
    static float pokemonCentreY(int stripTop, float size, PlaqueConfig cfg) {
        return stripTop + HEIGHT - size * 0.9f - cfg.pokemonLift;
    }

    static float trainerScale(PlaqueConfig cfg) { return TRAINER_BASE_SCALE * cfg.trainerSize / 100f; }

    /** Where the top of the trainer's head is drawn. */
    static int trainerTop(int stripTop, PlaqueConfig cfg) { return stripTop + PAD - cfg.trainerLift; }
}
