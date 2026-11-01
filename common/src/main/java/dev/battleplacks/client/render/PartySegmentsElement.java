package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.config.PlaqueConfig;

/** The plain bar style: a dark bar with one straight, evenly sized segment per party slot, in the card's accent colour. */
final class PartySegmentsElement extends PartyElement {
    private static final int BAR_H = 6, SEGMENT_W = 17, GAP = 2, PAD = 2;
    private static final int BAR_COLOR = 0xCC11141C;

    PartySegmentsElement(PlaqueModel model, PlaqueConfig cfg) {
        super(model, cfg);
    }

    @Override
    public int height() { return BAR_H + 3; }

    @Override
    protected void drawBackground(CardContext c, int y) {
        int w = count() * (SEGMENT_W + GAP) - GAP + PAD * 2;
        int x = c.edgeX(0, w);
        c.g().fill(x, y, x + w, y + BAR_H, c.fade(BAR_COLOR));
    }

    @Override
    protected void drawSlot(CardContext c, Slot slot, int index, int y) {
        int color = c.fade(slot.state() == PlaqueModel.SlotState.HEALTHY ? c.accent() : slot.state().color());
        int x = c.edgeX(PAD + index * (SEGMENT_W + GAP), SEGMENT_W);
        c.g().fill(x, y + 2, x + SEGMENT_W, y + BAR_H - 2, color);
    }
}
