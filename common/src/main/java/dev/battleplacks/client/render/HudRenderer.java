package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.Anchor;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/** Lays out one card per trainer in their corner and draws it. Mirrors preview/index.html. */
public final class HudRenderer {
    private final CardFactory factory = new CardFactory(new TrainerEntityPainter());

    public void render(GuiGraphics g, List<PlaqueModel> models, PlaqueConfig cfg, float partial) {
        Theme.Palette palette = cfg.theme.palette(cfg.opacity);
        AnchorStacker stacker = new AnchorStacker();

        for (PlaqueModel model : models) {
            if (model.kind() == PlaqueModel.Kind.WILD && model.active().isEmpty()) continue;
            List<CardElement> elements = factory.build(model, cfg);
            int height = elements.stream().mapToInt(CardElement::height).sum();

            Anchor anchor = cfg.anchorFor(model.own());
            int used = Math.round(stacker.reserve(anchor, Math.round(height * cfg.scale)) / cfg.scale);
            int x = anchor.isLeft() ? cfg.margin : Math.round(g.guiWidth() / cfg.scale) - CardContext.WIDTH - cfg.margin;
            int y = anchor.isTop() ? cfg.margin + used
                    : Math.round(g.guiHeight() / cfg.scale) - height - cfg.margin - used;

            CardContext ctx = new CardContext(g, Minecraft.getInstance().font, cfg, palette,
                    palette.accent(model.own(), cfg), anchor.isLeft(), partial, x);
            g.pose().pushPose();
            g.pose().scale(cfg.scale, cfg.scale, 1f);
            for (CardElement element : elements) {
                element.draw(ctx, y);
                y += element.height();
            }
            g.pose().popPose();
        }
    }
}
