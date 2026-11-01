package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * The compact strip under Cobblemon's own tile: a big trainer leaning in front of the active Pokemon, both aligned to
 * the top of the strip and cut off at its bottom edge (like the reference). The background texture is optional.
 */
final class StripElement implements CardElement {
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("battleplacks", "textures/gui/strip.png");
    private static final int HEIGHT = 44, PAD = 3;
    private static final int TRAINER_OFFSET = 28, POKEMON_OFFSET = 44;
    private static final float TRAINER_SCALE = 50f;

    private final PlaqueModel model;
    private final TrainerEntityPainter painter;

    StripElement(PlaqueModel model, TrainerEntityPainter painter) {
        this.model = model;
        this.painter = painter;
    }

    @Override
    public int height() { return HEIGHT; }

    @Override
    public void draw(CardContext c, int y) {
        GuiGraphics g = c.g();
        if (c.cfg().showStripBackground) {
            g.setColor(1f, 1f, 1f, c.alpha());
            g.blit(BACKGROUND, c.x(), y, 0, 0, CardContext.WIDTH, HEIGHT, CardContext.WIDTH, HEIGHT);
            g.setColor(1f, 1f, 1f, 1f);
        }
        g.enableScissor(c.x() + 1, y + 1, c.x() + CardContext.WIDTH - 1, y + HEIGHT);
        drawPokemon(c, y);
        drawTrainer(c, y);
        g.disableScissor();
    }

    /** Pokemon first so the trainer stands in front. The portrait is centred in its square, so the square starts above the strip. */
    private void drawPokemon(CardContext c, int y) {
        int count = model.active().size();
        float size = count > 1 ? 17 : 22; // about the size of a Cobblemon tile portrait
        for (int i = 0; i < count; i++) {
            var portrait = model.active().get(i).portrait();
            if (portrait == null) continue;
            float cx = c.edgeX(POKEMON_OFFSET + Math.round(i * (size + 6)), Math.round(size)) + size / 2;
            // TODO(visual): check the Pokemon's top lines up with the top of the strip.
            portrait.draw(c.g(), cx, y + HEIGHT - size * 0.9f, size, !c.left(), c.pokemonPartial());
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
