package dev.battleplacks.client.render;

import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Theme;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/** Everything an element needs to draw itself in one card. Mirroring for right-hand cards lives here, once. */
public record CardContext(GuiGraphics g, Font font, PlaqueConfig cfg, Theme.Palette palette, int accent,
                          boolean left, float partial, int x) {
    public static final int WIDTH = 150;

    /** X for something {@code width} wide placed {@code offset} from the card's near edge. */
    public int edgeX(int offset, int width) {
        return left ? x + offset : x + WIDTH - offset - width;
    }

    /** X for text so it hugs the card's near edge. */
    public int textX(String text, int offset) {
        return edgeX(offset, font.width(text));
    }
}
