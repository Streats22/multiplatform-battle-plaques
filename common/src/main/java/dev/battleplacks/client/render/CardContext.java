package dev.battleplacks.client.render;

import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Theme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** Everything an element needs to draw itself in one card. Mirroring for right-hand cards lives here, once. */
public record CardContext(GuiGraphics g, Font font, PlaqueConfig cfg, Theme.Palette palette, int accent,
                          boolean left, float partial, int x, float alpha) {
    public static final int WIDTH = 140;

    /** Applies the card's fade to an ARGB colour. */
    public int fade(int argb) {
        int a = Math.round((argb >>> 24) * alpha);
        return (a << 24) | (argb & 0xFFFFFF);
    }

    /** X for something {@code width} wide placed {@code offset} from the card's near edge. */
    public int edgeX(int offset, int width) {
        return left ? x + offset : x + WIDTH - offset - width;
    }

    /** X for text so it hugs the card's near edge. */
    public int textX(String text, int offset) {
        return edgeX(offset, font.width(text));
    }
}
