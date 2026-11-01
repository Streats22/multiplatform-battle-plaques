package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * The compact strip under Cobblemon's own tile: the trainer and the active Pokemon, both aligned to the top of the
 * strip and cut off at its bottom edge, over a background texture a resource pack can replace.
 */
final class StripElement implements CardElement {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("battleplacks", "textures/gui/strip.png");
    private static final int HEIGHT = 44, GAP = 2, PAD = 3;
    private static final int TRAINER_OFFSET = 24, POKEMON_OFFSET = 52;
    private static final float TRAINER_SCALE = 40f;

    private final PlaqueModel model;
    private final TrainerEntityPainter painter;

    StripElement(PlaqueModel model, TrainerEntityPainter painter) {
        this.model = model;
        this.painter = painter;
    }

    @Override
    public int height() { return HEIGHT + GAP; }

    @Override
    public void draw(CardContext c, int y) {
        GuiGraphics g = c.g();
        if (c.cfg().showStripBackground) {
            g.blit(BACKGROUND, c.x(), y, 0, 0, CardContext.WIDTH, HEIGHT, CardContext.WIDTH, HEIGHT);
        }
        g.enableScissor(c.x() + 1, y + 1, c.x() + CardContext.WIDTH - 1, y + HEIGHT);
        drawPokemon(c, y);
        drawTrainer(c, y);
        g.disableScissor();
    }

    /** Pokemon first so the trainer stands in front. The portrait is centred in its square, so the square starts above the strip. */
    private void drawPokemon(CardContext c, int y) {
        int count = model.active().size();
        float size = count > 1 ? 50 : 64;
        for (int i = 0; i < count; i++) {
            var portrait = model.active().get(i).portrait();
            if (portrait == null) continue;
            float cx = c.edgeX(POKEMON_OFFSET + Math.round(i * (size - 16)), Math.round(size)) + size / 2;
            // TODO(visual): check the Pokemon's top lines up with the top of the strip.
            portrait.draw(c.g(), cx, y + size / 2 - 6, size, !c.left(), c.partial());
        }
    }

    /** Head at the top of the strip, body running down until the strip ends. */
    private void drawTrainer(CardContext c, int y) {
        int x = c.edgeX(TRAINER_OFFSET, 0);
        painter.find(model.entityId()).ifPresentOrElse(
                e -> painter.paintHangingFrom(c, e, x, y + PAD, TRAINER_SCALE, c.cfg().stanceFor(model.own())),
                () -> c.g().fill(x - 8, y + PAD, x + 8, y + PAD + 16, 0xFF55607A));
    }
}
