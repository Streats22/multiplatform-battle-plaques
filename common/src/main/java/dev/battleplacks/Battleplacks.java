package dev.battleplacks;

import dev.battleplacks.client.BattleSource;
import dev.battleplacks.client.cobblemon.CobblemonBattleSource;
import dev.battleplacks.client.render.HudRenderer;
import dev.battleplacks.config.PlaqueConfig;
import net.minecraft.client.gui.GuiGraphics;

/** Composition root: wires the battle source, the renderer and the config together. Mixins talk only to this. */
public final class Battleplacks {
    private static final Battleplacks INSTANCE = new Battleplacks(new CobblemonBattleSource(), new HudRenderer());

    private final BattleSource source;
    private final HudRenderer renderer;

    Battleplacks(BattleSource source, HudRenderer renderer) {
        this.source = source;
        this.renderer = renderer;
    }

    public static Battleplacks get() { return INSTANCE; }

    /** Draws the cards for the current battle, if any. */
    public void render(GuiGraphics graphics, float partialTick) {
        PlaqueConfig cfg = PlaqueConfig.get();
        if (!cfg.enabled || !source.inBattle()) return;
        renderer.render(graphics, source.snapshot(), cfg, partialTick);
    }

    /** True while Cobblemon's own battle tiles should be suppressed. */
    public boolean replacesDefaultTiles() {
        PlaqueConfig cfg = PlaqueConfig.get();
        return cfg.enabled && cfg.replaceDefaultTiles && source.inBattle();
    }
}
