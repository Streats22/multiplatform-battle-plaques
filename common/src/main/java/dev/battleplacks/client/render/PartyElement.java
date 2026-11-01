package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.config.PlaqueConfig;

import java.util.List;

/** Shared behaviour for party displays: which slots show, and drawing them from the card's near edge. */
abstract class PartyElement implements CardElement {
    private final List<Slot> slots;

    protected PartyElement(PlaqueModel model, PlaqueConfig cfg) {
        this.slots = model.party().stream()
                .filter(s -> s.state() != PlaqueModel.SlotState.UNKNOWN || cfg.showUnknownSlots || model.own())
                .toList();
    }

    @Override
    public final void draw(CardContext c, int y) {
        if (slots.isEmpty()) return;
        int rowY = y + rowOffset();
        drawBackground(c, rowY);
        for (int i = 0; i < slots.size(); i++) {
            // right-hand cards read from the screen edge inwards, so the order is mirrored
            Slot slot = slots.get(c.left() ? i : slots.size() - 1 - i);
            drawSlot(c, slot, i, rowY);
        }
    }

    /** Pips follow the original breathing room; the compact bar sits directly beneath the model strip. */
    protected int rowOffset() { return 2; }

    protected int count() { return slots.size(); }

    protected void drawBackground(CardContext c, int y) {}

    protected abstract void drawSlot(CardContext c, Slot slot, int index, int y);
}
