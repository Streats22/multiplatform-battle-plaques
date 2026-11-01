package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.Anchor;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * Lays out one card per trainer in their corner and draws it. A battle runs through up to three phases: the
 * optional full-screen VS intro, the strip (which fades after a few seconds unless disabled), and nothing.
 */
public final class HudRenderer {
    /** Cobblemon's own tile: top margin and the height of each tile in a side's stack. */
    private static final int COBBLEMON_TOP = 4, COBBLEMON_TILE_STEP = 41;
    private static final float FADE_SECONDS = 1f;

    private final TrainerEntityPainter painter = new TrainerEntityPainter();
    private final CardFactory factory = new CardFactory(painter);
    private final VsScreen vsScreen = new VsScreen(painter);

    /** @param elapsed seconds since the battle started */
    public void render(GuiGraphics g, List<PlaqueModel> models, PlaqueConfig cfg, float partial, float elapsed) {
        float introLength = cfg.introEnabled ? cfg.introSeconds : 0f;
        boolean intro = elapsed < introLength;
        float introProgress = intro ? elapsed / introLength : 0f;
        Theme.Palette palette = cfg.theme.palette(cfg.opacity);
        if (intro) {
            vsScreen.render(g, models, cfg, palette, introProgress, partial);
            return;
        }
        float alpha = stripAlpha(cfg, elapsed - introLength);
        if (alpha <= 0.02f) return;

        AnchorStacker stacker = new AnchorStacker();

        for (PlaqueModel model : models) {
            if (model.kind() == PlaqueModel.Kind.WILD && model.active().isEmpty()) continue;
            CardLook look = CardLook.of(model, cfg, palette);
            List<CardElement> elements = factory.build(model, cfg, look);
            int height = elements.stream().mapToInt(CardElement::height).sum();

            Anchor anchor = cfg.anchorFor(model.own());
            int used = Math.round(stacker.reserve(anchor, Math.round(height * cfg.scale)) / cfg.scale);
            int x = anchor.isLeft() ? cfg.margin : Math.round(g.guiWidth() / cfg.scale) - CardContext.WIDTH - cfg.margin;
            int y = anchor.isTop() ? cfg.margin + reservedTop(model, cfg) + used
                    : Math.round(g.guiHeight() / cfg.scale) - height - cfg.margin - used;

            CardContext ctx = new CardContext(g, Minecraft.getInstance().font, cfg, palette,
                    look.accent(), anchor.isLeft(), partial, x, alpha);
            g.pose().pushPose();
            g.pose().scale(cfg.scale, cfg.scale, 1f);
            // The 3D models cannot be made transparent, so while fading the whole card also slides off its edge.
            float slide = (1f - alpha) * (CardContext.WIDTH + 20);
            g.pose().translate(anchor.isLeft() ? -slide : slide, 0, 0);
            for (CardElement element : elements) {
                element.draw(ctx, y);
                y += element.height();
            }
            g.pose().popPose();
        }
    }

    /** 1 while the strip is visible, then eases to 0 over a second. Stays 1 when fading is switched off. */
    private static float stripAlpha(PlaqueConfig cfg, float age) {
        if (!cfg.stripFades || age <= cfg.stripSeconds) return 1f;
        return Math.max(0f, 1f - (age - cfg.stripSeconds) / FADE_SECONDS);
    }

    /** Space taken at the top by Cobblemon's own tiles, so the strip sits right underneath them. */
    private static int reservedTop(PlaqueModel model, PlaqueConfig cfg) {
        if (cfg.showTiles) return 0;
        return COBBLEMON_TOP + COBBLEMON_TILE_STEP * Math.max(1, model.active().size()) + cfg.stripOffset - cfg.margin;
    }
}
