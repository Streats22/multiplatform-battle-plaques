package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.config.PlaqueConfig;
import net.minecraft.resources.ResourceLocation;

/**
 * Segmented party bar: a frame texture (replaceable by a resource pack) with six cells, and one colour segment drawn
 * inside each cell, in the accent colour while healthy. The cell grid is six equal columns across the 140 px frame.
 */
final class PartyBarElement extends PartyElement {
    private static final ResourceLocation FRAME =
            ResourceLocation.fromNamespaceAndPath("battleplacks", "textures/gui/party_bar.png");
    private static final int HEIGHT = 8, INSET = 2, CELLS = 6;

    PartyBarElement(PlaqueModel model, PlaqueConfig cfg) {
        super(model, cfg);
    }

    @Override
    public int height() { return HEIGHT + 2; }

    @Override
    protected void drawBackground(CardContext c, int y) {
        c.g().setColor(1f, 1f, 1f, c.alpha());
        c.g().blit(FRAME, c.x(), y, 0, 0, CardContext.WIDTH, HEIGHT, CardContext.WIDTH, HEIGHT);
        c.g().setColor(1f, 1f, 1f, 1f);
    }

    @Override
    protected void drawSlot(CardContext c, Slot slot, int index, int y) {
        int cellStart = Math.round(index * (float) CardContext.WIDTH / CELLS);
        int cellEnd = Math.round((index + 1) * (float) CardContext.WIDTH / CELLS);
        int width = cellEnd - cellStart - 2 * INSET;
        int x = c.edgeX(cellStart + INSET, width);
        int color = slot.state() == PlaqueModel.SlotState.HEALTHY ? c.accent() : slot.state().color();
        c.g().fill(x, y + INSET, x + width, y + HEIGHT - INSET, c.fade(color));
    }
}
