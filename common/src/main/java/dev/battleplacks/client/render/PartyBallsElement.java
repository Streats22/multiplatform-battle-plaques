package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.client.PlaqueModel.SlotState;
import dev.battleplacks.config.PlaqueConfig;

/** One small poke ball per slot; fainted and unknown slots are greyed. */
final class PartyBallsElement extends PartyElement {
    private static final int SIZE = 9, STEP = 11;

    PartyBallsElement(PlaqueModel model, PlaqueConfig cfg) {
        super(model, cfg);
    }

    @Override
    public int height() { return 14; }

    @Override
    protected void drawSlot(CardContext c, Slot slot, int index, int y) {
        int x = c.edgeX(index * STEP, SIZE);
        int color = slot.state().color();
        boolean flat = slot.state() == SlotState.UNKNOWN || slot.state() == SlotState.FAINTED;
        c.g().fill(x, y, x + SIZE, y + SIZE, 0xFF111111);
        c.g().fill(x + 1, y + 1, x + 8, y + 4, flat ? color : 0xFFE53935);
        c.g().fill(x + 1, y + 5, x + 8, y + 8, flat ? color : 0xFFF2F2F2);
        c.g().fill(x + 3, y + 3, x + 6, y + 6, 0xFF111111);
        if (!flat) c.g().fill(x + 4, y + 4, x + 5, y + 5, color);
    }
}
