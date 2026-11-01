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
        List<Slot> party,
        List<Active> active
) {
    public enum Kind { PLAYER, NPC, WILD }

    public enum SlotState {
        HEALTHY(0xFF4ADE80), STATUS(0xFFFACC15), FAINTED(0xFF7F1D1D), UNKNOWN(0xFF2A2F3A);

        private final int color;

        SlotState(int color) { this.color = color; }

        public int color() { return color; }
    }

    public record Slot(SlotState state) {}

    /** Draws a Pokémon model centred on (cx, cy) fitting a square of the given size, in current GUI units. */
    @FunctionalInterface
    public interface PortraitDrawer {
        void draw(GuiGraphics g, float cx, float cy, float size, boolean reversed, float partialTicks);
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
