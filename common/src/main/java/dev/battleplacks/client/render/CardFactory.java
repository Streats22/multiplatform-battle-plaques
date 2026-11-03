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
        CardElement party = !cfg.showParty ? null : switch (cfg.ballStyle) {
            case BALLS -> new PartyBallsElement(model, cfg);
            case PIPS -> new PartyBarElement(model, cfg);
        };
        // The trainer and Pokémon continue down behind the party bar (HudRenderer draws later elements in front)
        if (cfg.showHead) elements.add(new StripElement(model, painter, party == null ? 0 : party.height() - 1));
        if (party != null) elements.add(party);
        if (cfg.showTrainerName) elements.add(new TeamLabelElement(model));
        return elements;
    }
}
