package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;

/** "Team <name>" for players, the trainer's own name for NPCs. */
final class TeamLabelElement implements CardElement {
    private final String label;

    TeamLabelElement(PlaqueModel model) {
        this.label = model.kind() == PlaqueModel.Kind.NPC ? model.name() : "Team " + model.name();
    }

    @Override
    public int height() { return 14; }

    @Override
    public void draw(CardContext c, int y) {
        c.g().drawString(c.font(), label, c.textX(label, 0), y + 3, c.fade(0xFFFFFFFF), true);
    }
}
