package dev.battleplacks.client;

import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.UUID;

/**
 * Plain snapshot of one trainer's team card. The renderer only knows this model, so it has no compile
 * dependency on Cobblemon; {@link CobblemonBattleAdapter} is the only class that touches Cobblemon.
 */
public record PlaqueModel(
        Kind kind,
        String name,
        /** Player UUID, or the trainer entity UUID for NPCs (RCT). Used to find the entity to draw in 3D. */
        UUID entityId,
        boolean own,
        /** The whole team in party order, always 6 entries; unknown opponent Pokémon are UNKNOWN with no portrait. */
        List<TeamMember> team,
        List<Active> active
) {
    public enum Kind { PLAYER, NPC, WILD }

    /** The team as plain slots, for the party bar. */
    public List<Slot> party() {
        return team.stream().map(m -> new Slot(m.state())).toList();
    }

    /**
     * One Pokémon of a team. {@code id} is null and {@code portrait} null for a slot nobody has seen yet.
     * {@code active} is true while it is on the field.
     */
    public record TeamMember(UUID id, SlotState state, boolean active, PortraitDrawer portrait) {
        public static TeamMember unknown() { return new TeamMember(null, SlotState.UNKNOWN, false, null); }
    }

    public enum SlotState {
        HEALTHY(0xFF4ADE80), STATUS(0xFFFACC15), FAINTED(0xFF7F1D1D), UNKNOWN(0xFF2A2F3A);

        private final int color;

        SlotState(int color) { this.color = color; }

        public int color() { return color; }
    }

    public record Slot(SlotState state) {}

    /**
     * Draws a Pokémon model horizontally centred on cx, with the top of the model at top, in current GUI units.
     * The model hangs down from there and is clipped to a box about 1.8 * size tall.
     */
    @FunctionalInterface
    public interface PortraitDrawer {
        void draw(GuiGraphics g, float cx, float top, float size, boolean reversed, float partialTicks);
    }

    public record Active(
            String name,
            int level,
            /** 'M', 'F' or '-' */
            char gender,
            float hpFraction,
            int hp,
            int maxHp,
            /** true when hp/maxHp are real numbers (own Pokémon), false when only a percentage is known */
            boolean flatHp,
            /** Showdown status id (brn, par, psn, tox, slp, frz) or null */
            String status,
            PortraitDrawer portrait
    ) {}
}
