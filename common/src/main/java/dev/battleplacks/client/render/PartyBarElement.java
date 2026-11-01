package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.config.PlaqueConfig;
import net.minecraft.resources.ResourceLocation;

/**
 * Cobblemon's own tile bar, reused: the dark band along the top of battle_info_base.png, with the coloured slash
 * block from battle_info_role.png on it. The party is drawn as small slanted pips on the rest of the band.
 * Right-hand cards use Cobblemon's flipped textures.
 */
final class PartyBarElement extends PartyElement {
    private static final ResourceLocation BASE = cobblemon("battle_info_base.png");
    private static final ResourceLocation BASE_FLIPPED = cobblemon("battle_info_base_flipped.png");
    private static final ResourceLocation ROLE = cobblemon("battle_info_role.png");
    private static final ResourceLocation ROLE_FLIPPED = cobblemon("battle_info_role_flipped.png");

    /** Rows of the base texture that form the dark band (5) plus the light line under it (1). */
    private static final int BAND_H = 6, BASE_TEX_W = 140, BASE_TEX_H = 40;
    private static final int ROLE_W = 27, ROLE_H = 3, ROLE_TEX_H = 12, ROLE_OFFSET = 12;
    private static final int PIP_START = 44, PIP_CELL = 15, PIP_W = 12, PIP_H = 3;

    PartyBarElement(PlaqueModel model, PlaqueConfig cfg) {
        super(model, cfg);
    }

    private static ResourceLocation cobblemon(String file) {
        return ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/gui/battle/" + file);
    }

    @Override
    public int height() { return BAND_H + 3; }

    @Override
    protected void drawBackground(CardContext c, int y) {
        c.tint(0xFFFFFFFF);
        c.g().blit(c.left() ? BASE : BASE_FLIPPED, c.x(), y, 0, 0, BASE_TEX_W, BAND_H, BASE_TEX_W, BASE_TEX_H);
        c.tint(c.accent());
        c.g().blit(c.left() ? ROLE : ROLE_FLIPPED, c.edgeX(ROLE_OFFSET, ROLE_W), y + 1, 0, 0, ROLE_W, ROLE_H, ROLE_W, ROLE_TEX_H);
        c.untint();
    }

    @Override
    protected void drawSlot(CardContext c, Slot slot, int index, int y) {
        int color = c.fade(slot.state() == PlaqueModel.SlotState.HEALTHY ? c.accent() : slot.state().color());
        for (int row = 0; row < PIP_H; row++) {
            // one pixel shift per row gives the slanted look; mirrored on right-hand cards
            int shift = c.left() ? PIP_H - 1 - row : row;
            int x = c.edgeX(PIP_START + index * PIP_CELL + shift, PIP_W - PIP_H);
            c.g().fill(x, y + 1 + row, x + PIP_W - PIP_H, y + 2 + row, color);
        }
    }
}
