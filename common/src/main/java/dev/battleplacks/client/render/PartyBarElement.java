package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.config.PlaqueConfig;

/**
 * Party bar in the style of Cobblemon's own tile bar: a dark track with a light top edge and slanted (parallelogram)
 * segments, one per slot, in the side colour while healthy.
 */
final class PartyBarElement extends PartyElement {
    private static final int TRACK_H = 7, SEGMENT_H = 4, CELLS = 6, PAD = 2, GAP = 3;
    private static final int TRACK = 0xF02B2B30, TRACK_TOP = 0xFF4C4C54, OUTLINE = 0xFF121216;

    PartyBarElement(PlaqueModel model, PlaqueConfig cfg) {
        super(model, cfg);
    }

    @Override
    public int height() { return TRACK_H + GAP; }

    @Override
    protected void drawBackground(CardContext c, int y) {
        int x = c.x(), w = CardContext.WIDTH;
        c.g().fill(x, y, x + w, y + TRACK_H, c.fade(OUTLINE));
        c.g().fill(x + 1, y + 1, x + w - 1, y + TRACK_H - 1, c.fade(TRACK));
        c.g().fill(x + 1, y + 1, x + w - 1, y + 2, c.fade(TRACK_TOP));
    }

    @Override
    protected void drawSlot(CardContext c, Slot slot, int index, int y) {
        int cell = (CardContext.WIDTH - 2 * PAD) / CELLS;
        int width = cell - 3;
        int color = c.fade(slot.state() == PlaqueModel.SlotState.HEALTHY ? c.accent() : slot.state().color());
        int top = y + 2;
        for (int row = 0; row < SEGMENT_H; row++) {
            // each row shifts one pixel, giving the slanted look; mirrored on right-hand cards
            int shift = c.left() ? SEGMENT_H - 1 - row : row;
            int x = c.edgeX(PAD + index * cell + shift, width - SEGMENT_H);
            c.g().fill(x, top + row, x + width - SEGMENT_H, top + row + 1, color);
        }
    }
}
