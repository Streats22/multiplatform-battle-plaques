package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.team.TeamNameRules;
import dev.battleplacks.team.TeamNames;

/** "Team <name>" for players, the trainer's own name for NPCs. */
final class TeamLabelElement implements CardElement {
    private final String label;

    TeamLabelElement(PlaqueModel model) {
        this.label = labelFor(model);
    }

    /** NPC trainers keep their own name; players show their chosen team name, else "Team <name>". */
    static String labelFor(PlaqueModel model) {
        if (model.kind() == PlaqueModel.Kind.NPC) return model.name();
        String chosen = model.own()
                ? TeamNameRules.validate(PlaqueConfig.get().teamName).name()
                : TeamNames.get(model.entityId()).orElse("");
        return chosen.isEmpty() ? "Team " + model.name() : chosen;
    }

    @Override
    public int height() { return 14; }

    @Override
    public void draw(CardContext c, int y) {
        c.g().drawString(c.font(), label, c.textX(label, 0), y + 3, c.fade(0xFFFFFFFF), true);
    }
}
