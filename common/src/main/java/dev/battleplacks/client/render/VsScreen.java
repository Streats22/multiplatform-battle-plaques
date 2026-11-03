package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * The full-screen "versus" intro, in the spirit of the Pokemon games: a shiny band in each side's colour crossing the
 * screen, both trainers and their Pokemon on top, names, and a big VS. The bands slide in, hold, then slide out.
 */
final class VsScreen {
    private static final float IN = 0.22f, OUT_START = 0.82f;

    private final TrainerEntityPainter painter;

    VsScreen(TrainerEntityPainter painter) {
        this.painter = painter;
    }

    /** @param progress 0..1 across the whole intro */
    void render(GuiGraphics g, List<PlaqueModel> models, PlaqueConfig cfg, Theme.Palette palette, float progress, float partial) {
        PlaqueModel own = first(models, true), foe = first(models, false);
        if (own == null || foe == null) return;

        int sw = g.guiWidth(), sh = g.guiHeight();
        float in = ease(Math.min(1f, progress / IN));
        float out = ease(Math.max(0f, (progress - OUT_START) / (1f - OUT_START)));
        float visible = in * (1f - out);
        if (visible <= 0.01f) return;

        int bandH = sh * 30 / 100, bandY = sh * 36 / 100;
        int slide = Math.round((1f - in) * sw * 0.6f + out * sw * 0.6f);
        int ownColor = palette.accent(true, cfg), foeColor = palette.accent(false, cfg);

        g.fill(0, 0, sw, sh, ((int) (0x70 * visible) << 24));
        drawBand(g, -slide, sw / 2 + 12 - slide, bandY, bandH, ownColor, visible);
        drawBand(g, sw / 2 - 12 + slide, sw + slide, bandY, bandH, foeColor, visible);
        drawDivider(g, sw / 2, bandY, bandH, visible);

        drawSide(g, own, true, cfg, palette, ownColor, sw, sh, bandY, bandH, -slide, visible, partial);
        drawSide(g, foe, false, cfg, palette, foeColor, sw, sh, bandY, bandH, slide, visible, partial);
        drawVs(g, sw, bandY + bandH, in, visible);
    }

    private void drawSide(GuiGraphics g, PlaqueModel m, boolean left, PlaqueConfig cfg, Theme.Palette palette, int color,
                          int sw, int sh, int bandY, int bandH, int dx, float visible, float partial) {
        Font font = Minecraft.getInstance().font;
        CardContext c = new CardContext(g, font, cfg, palette, color, left, partial, 0, visible);

        int trainerX = Math.round(sw * (left ? 0.20f : 0.80f)) + dx;
        painter.find(m.entityId()).ifPresent(e ->
                painter.paintStandingOn(c, e, trainerX, bandY + bandH + 8, sh * 0.23f, m.own()));

        float size = sh * 0.17f;
        int pokemonX = Math.round(sw * (left ? 0.36f : 0.64f)) + dx;
        for (int i = 0; i < m.active().size(); i++) {
            var portrait = m.active().get(i).portrait();
            if (portrait != null) {
                portrait.draw(g, pokemonX + (left ? 1 : -1) * i * size * 0.9f, bandY + bandH - size * 1.2f, size, !left, c.pokemonPartial());
            }
        }

        String name = m.kind() == PlaqueModel.Kind.NPC ? m.name() : TeamLabelElement.labelFor(m);
        float scale = 1.6f;
        int textW = Math.round(font.width(name) * scale);
        int textX = left ? Math.round(sw * 0.05f) + dx : sw - Math.round(sw * 0.05f) - textW + dx;
        g.pose().pushPose();
        g.pose().translate(textX, bandY - 18, 0);
        g.pose().scale(scale, scale, 1f);
        g.drawString(font, name, 0, 0, c.fade(0xFFFFFFFF), true);
        g.pose().popPose();
    }

    /** Vertical metallic gradient with a thin sheen line, like the shiny bars in the games. */
    private static void drawBand(GuiGraphics g, int x1, int x2, int y, int h, int color, float visible) {
        int dark = scale(color, 0.45f, visible), bright = scale(color, 1.1f, visible), dim = scale(color, 0.38f, visible);
        g.fillGradient(x1, y, x2, y + h / 2, dark, bright);
        g.fillGradient(x1, y + h / 2, x2, y + h, bright, dim);
        g.fill(x1, y + h * 3 / 10, x2, y + h * 3 / 10 + 1, ((int) (0x55 * visible) << 24) | 0xFFFFFF);
        g.fill(x1, y + h - 2, x2, y + h, ((int) (0x66 * visible) << 24));
    }

    /** A slanted white strip between the two bands. */
    private static void drawDivider(GuiGraphics g, int cx, int y, int h, float visible) {
        int white = ((int) (0xE0 * visible) << 24) | 0xFFFFFF;
        for (int row = 0; row < h; row++) {
            int x = cx + (h / 2 - row) / 3;
            g.fill(x - 2, y + row, x + 2, y + row + 1, white);
        }
    }

    private static void drawVs(GuiGraphics g, int sw, int y, float in, float visible) {
        Font font = Minecraft.getInstance().font;
        float scale = 5f * (0.6f + 0.4f * in);
        int w = Math.round(font.width("VS") * scale);
        g.pose().pushPose();
        g.pose().translate(sw / 2f - w / 2f, y + 4, 0);
        g.pose().scale(scale, scale, 1f);
        int outline = ((int) (0xFF * visible) << 24) | 0x14202B;
        for (int[] o : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {-1, 1}, {1, -1}}) {
            g.drawString(font, "VS", o[0], o[1], outline, false);
        }
        g.drawString(font, "VS", 0, 0, ((int) (0xFF * visible) << 24) | 0xFFFFFF, false);
        g.pose().popPose();
    }

    private static PlaqueModel first(List<PlaqueModel> models, boolean own) {
        return models.stream().filter(m -> m.own() == own).findFirst().orElse(null);
    }

    private static int scale(int argb, float f, float alpha) {
        int r = Math.min(255, Math.round(((argb >> 16) & 255) * f));
        int gr = Math.min(255, Math.round(((argb >> 8) & 255) * f));
        int b = Math.min(255, Math.round((argb & 255) * f));
        return ((int) (0xFF * alpha) << 24) | (r << 16) | (gr << 8) | b;
    }

    private static float ease(float t) { return 1f - (1f - t) * (1f - t) * (1f - t); }
}
