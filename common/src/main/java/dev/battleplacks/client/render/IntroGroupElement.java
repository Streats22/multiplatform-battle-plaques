package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;

/** Full-size trainer and active Pokémon, shown only during the optional battle intro. */
final class IntroGroupElement implements CardElement {
    private static final int HEIGHT = 84, GAP = 4, TRAINER_OFFSET = 20, POKEMON_OFFSET = 52;

    private final PlaqueModel model;
    private final TrainerEntityPainter painter;

    IntroGroupElement(PlaqueModel model, TrainerEntityPainter painter) {
        this.model = model;
        this.painter = painter;
    }

    @Override
    public int height() { return HEIGHT + GAP; }

    @Override
    public void draw(CardContext c, int y) {
        int top = y + 2;
        int trainerX = c.edgeX(TRAINER_OFFSET, 0);
        painter.find(model.entityId()).ifPresentOrElse(
                e -> painter.paintStandingOn(c, e, trainerX, top + HEIGHT - 2, 38f, c.cfg().stanceFor(model.own())),
                () -> c.g().fill(trainerX - 8, top + HEIGHT - 40, trainerX + 8, top + HEIGHT - 24, 0xFF55607A));

        int count = model.active().size();
        float size = count > 1 ? 26 : 34;
        for (int i = 0; i < count; i++) {
            var portrait = model.active().get(i).portrait();
            if (portrait == null) continue;
            float cx = c.edgeX(POKEMON_OFFSET + Math.round(i * (size - 6)), Math.round(size)) + size / 2;
            portrait.draw(c.g(), cx, top + HEIGHT - size / 2 - 2, size, !c.left(), c.pokemonPartial());
        }
    }
}
