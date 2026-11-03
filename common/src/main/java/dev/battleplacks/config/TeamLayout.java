package dev.battleplacks.config;

/** Which Pokémon a side's strip shows next to its trainer. See docs/TEAM_DISPLAY_PLAN.md. */
public enum TeamLayout {
    /** Only the Pokémon in battle (one, or two in doubles). */
    ACTIVE_ONLY,
    /** The Pokémon in battle at full size, the rest of the known team small beside it. */
    ACTIVE_AND_BENCH,
    /** The whole known team at one small size, the ones in battle marked. */
    FULL_TEAM,
    /** Your own chosen Pokémon (PlaqueConfig.pickedPokemon); the opponent's side uses ACTIVE_AND_BENCH. */
    MY_PICKS
}
