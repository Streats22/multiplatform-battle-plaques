package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.LabelPosition;
import dev.battleplacks.config.PlaqueConfig;

import java.util.ArrayList;
import java.util.List;

/** Decides which elements a card has, from the model and the user's config. */
final class CardFactory {
    private final TrainerEntityPainter painter;

    CardFactory(TrainerEntityPainter painter) {
        this.painter = painter;
    }

    List<CardElement> build(PlaqueModel model, PlaqueConfig cfg, CardLook look) {
        List<CardElement> elements = new ArrayList<>();
        if (cfg.showTiles) model.active().forEach(a -> elements.add(new TileElement(a)));
        if (cfg.showHead) elements.add(new StripElement(model, painter));
        CardElement label = cfg.showTrainerName ? new TeamLabelElement(model, look) : null;
        if (label != null && cfg.labelPosition == LabelPosition.ABOVE) elements.add(label);
        if (cfg.showParty) {
            elements.add(switch (look.style()) {
                case BALLS -> new PartyBallsElement(model, cfg);
                case SEGMENTED -> new PartySegmentsElement(model, cfg);
                case PIPS -> new PartyBarElement(model, cfg);
            });
        }
        if (label != null && cfg.labelPosition == LabelPosition.BELOW) elements.add(label);
        return elements;
    }
}
