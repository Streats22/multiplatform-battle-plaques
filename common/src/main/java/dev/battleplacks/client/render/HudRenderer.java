package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.Anchor;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/** Lays out one card per trainer in their corner and draws it. */
public final class HudRenderer {
    /** Cobblemon's own tile: top margin and the height of each tile in a side's stack. */
    private static final int COBBLEMON_TOP = 4, COBBLEMON_TILE_STEP = 41;

    private final CardFactory factory = new CardFactory(new TrainerEntityPainter());

    /** @param introProgress 0..1 while the battle intro plays, negative otherwise */
    public void render(GuiGraphics g, List<PlaqueModel> models, PlaqueConfig cfg, float partial, float introProgress) {
        Theme.Palette palette = cfg.theme.palette(cfg.opacity);
        boolean intro = introProgress >= 0f;
        AnchorStacker stacker = new AnchorStacker();

        for (PlaqueModel model : models) {
            if (model.kind() == PlaqueModel.Kind.WILD && model.active().isEmpty()) continue;
            List<CardElement> elements = factory.build(model, cfg, intro);
            int height = elements.stream().mapToInt(CardElement::height).sum();

            Anchor anchor = cfg.anchorFor(model.own());
            int used = Math.round(stacker.reserve(anchor, Math.round(height * cfg.scale)) / cfg.scale);
            int x = anchor.isLeft() ? cfg.margin : Math.round(g.guiWidth() / cfg.scale) - CardContext.WIDTH - cfg.margin;
            int y = anchor.isTop() ? cfg.margin + reservedTop(model, cfg) + used
                    : Math.round(g.guiHeight() / cfg.scale) - height - cfg.margin - used;

            CardContext ctx = new CardContext(g, Minecraft.getInstance().font, cfg, palette,
                    palette.accent(model.own(), cfg), anchor.isLeft(), partial, x);
            g.pose().pushPose();
            g.pose().scale(cfg.scale, cfg.scale, 1f);
            if (intro) g.pose().translate(slideOffset(introProgress, anchor.isLeft()), 0, 0);
            for (CardElement element : elements) {
                element.draw(ctx, y);
                y += element.height();
            }
            g.pose().popPose();
        }
    }

    /** Space taken at the top by Cobblemon's own tiles, so the strip sits right underneath them. */
    private static int reservedTop(PlaqueModel model, PlaqueConfig cfg) {
        if (cfg.showTiles || cfg.replaceDefaultTiles) return 0;
        return COBBLEMON_TOP + COBBLEMON_TILE_STEP * Math.max(1, model.active().size()) + cfg.stripOffset - cfg.margin;
    }

    /** Slides in from the screen edge during the first quarter of the intro, then holds. */
    private static float slideOffset(float progress, boolean left) {
        float t = Math.min(1f, progress * 4f);
        float eased = 1f - (1f - t) * (1f - t) * (1f - t);
        float distance = (1f - eased) * (CardContext.WIDTH + 40);
        return left ? -distance : distance;
    }
}
