package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.PlaqueConfig;

import java.util.ArrayList;
import java.util.List;

/** Decides which elements a card has, from the model and the user's config. */
final class CardFactory {
    private final TrainerEntityPainter painter;

    CardFactory(TrainerEntityPainter painter) {
        this.painter = painter;
    }

    List<CardElement> build(PlaqueModel model, PlaqueConfig cfg) {
        List<CardElement> elements = new ArrayList<>();
        if (cfg.showTiles) model.active().forEach(a -> elements.add(new TileElement(a)));
        if (cfg.showHead) elements.add(new StripElement(model, painter));
        if (cfg.showParty) {
            elements.add(switch (cfg.ballStyle) {
                case BALLS -> new PartyBallsElement(model, cfg);
                case PIPS -> new PartyBarElement(model, cfg);
            });
        }
        if (cfg.showTrainerName) elements.add(new TeamLabelElement(model));
        return elements;
    }
}
