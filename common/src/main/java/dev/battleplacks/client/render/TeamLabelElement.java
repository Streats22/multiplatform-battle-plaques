package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;

/** "Team <name>" for players, the trainer's own name for NPCs. */
final class TeamLabelElement implements CardElement {
    private final String label;

    TeamLabelElement(PlaqueModel model, CardLook look) {
        this.label = labelFor(model, look);
    }

    /** NPC trainers keep their own name; players show their chosen team name, else "Team <name>". */
    static String labelFor(PlaqueModel model, CardLook look) {
        if (model.kind() == PlaqueModel.Kind.NPC) return model.name();
        return look.teamName().isEmpty() ? "Team " + model.name() : look.teamName();
    }

    @Override
    public int height() { return 14; }

    @Override
    public void draw(CardContext c, int y) {
        c.g().drawString(c.font(), label, c.textX(label, 0), y + 3, c.fade(0xFFFFFFFF), true);
    }
}
