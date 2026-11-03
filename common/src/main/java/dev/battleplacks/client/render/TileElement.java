package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel.Active;
import net.minecraft.client.gui.GuiGraphics;

/** Cobblemon-style tile: portrait in an accent frame, name, level, gender and an HP bar. */
final class TileElement implements CardElement {
    private static final int TILE_H = 36, GAP = 2, FRAME = 30, PAD = 3;

    private final Active pokemon;

    TileElement(Active pokemon) {
        this.pokemon = pokemon;
    }

    @Override
    public int height() { return TILE_H + GAP; }

    @Override
    public void draw(CardContext c, int y) {
        GuiGraphics g = c.g();
        int x = c.x(), w = CardContext.WIDTH;
        g.fill(x, y, x + w, y + TILE_H, c.palette().panel());
        if (c.palette().hasEdge()) g.renderOutline(x, y, w, TILE_H, c.palette().edge());

        int frameX = c.edgeX(PAD, FRAME);
        g.fill(frameX, y + PAD, frameX + FRAME, y + PAD + FRAME, 0x59000000);
        g.renderOutline(frameX, y + PAD, FRAME, FRAME, c.accent());
        if (pokemon.portrait() != null) {
            pokemon.portrait().draw(g, frameX + FRAME / 2f, y + PAD + 1, FRAME - 2, !c.left(), c.partial());
        }

        int textW = w - FRAME - 14;
        int textX = c.left() ? frameX + FRAME + 4 : x + 4;
        drawHeader(c, textX, textW, y + 5);
        drawHp(c, textX, textW, y + 16);
    }

    private void drawHeader(CardContext c, int x, int width, int y) {
        String gender = pokemon.gender() == 'M' ? "♂" : pokemon.gender() == 'F' ? "♀" : "";
        String right = gender + (c.cfg().showLevel ? " Lv." + pokemon.level() : "");
        int genderColor = pokemon.gender() == 'M' ? 0xFF32CBFF : pokemon.gender() == 'F' ? 0xFFFC5454 : c.palette().subText();
        c.g().drawString(c.font(), pokemon.name(), x, y, c.palette().text(), false);
        c.g().drawString(c.font(), right, x + width - c.font().width(right), y, genderColor, false);
    }

    private void drawHp(CardContext c, int x, int width, int y) {
        float f = Math.max(0f, Math.min(1f, pokemon.hpFraction()));
        c.g().fill(x, y, x + width, y + 9, 0xFF14181F);
        c.g().fill(x, y, x + Math.round(width * f), y + 9, hpColor(f));
        if (c.cfg().showHpText) {
            String text = pokemon.flatHp() ? pokemon.hp() + "/" + pokemon.maxHp() : (int) Math.ceil(f * 100) + "%";
            c.g().drawString(c.font(), text, x + (width - c.font().width(text)) / 2, y + 1, 0xFFFFFFFF, true);
        }
        if (c.cfg().showStatus && pokemon.status() != null) {
            c.g().drawString(c.font(), pokemon.status().toUpperCase(), x, y + 11, 0xFFFACC15, false);
        }
    }

    private static int hpColor(float f) {
        return f > 0.5f ? 0xFF4ADE80 : f > 0.2f ? 0xFFFACC15 : 0xFFEF4444;
    }
}
