package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.config.PlaqueConfig;

/** Segmented party bar: one segment per slot, in the accent colour while healthy. */
final class PartyBarElement extends PartyElement {
    PartyBarElement(PlaqueModel model, PlaqueConfig cfg) {
        super(model, cfg);
    }

    @Override
    public int height() { return 8; }

    @Override
    protected void drawBackground(CardContext c, int y) {
        c.g().fill(c.x(), y, c.x() + CardContext.WIDTH, y + 6, 0x99000000);
    }

    @Override
    protected void drawSlot(CardContext c, Slot slot, int index, int y) {
        int segment = (CardContext.WIDTH - 2 - (count() - 1)) / count();
        int x = c.edgeX(1 + index * (segment + 1), segment);
        int color = slot.state() == PlaqueModel.SlotState.HEALTHY ? c.accent() : slot.state().color();
        c.g().fill(x, y + 1, x + segment, y + 5, color);
    }
}
