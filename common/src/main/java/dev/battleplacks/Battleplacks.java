package dev.battleplacks;

import dev.battleplacks.client.BattleFilter;
import dev.battleplacks.client.BattleSource;
import dev.battleplacks.client.BattleClock;
import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.cobblemon.CobblemonBattleSource;
import dev.battleplacks.client.render.HudRenderer;
import dev.battleplacks.config.PlaqueConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

/** Composition root: wires the battle source, filter, renderer and config together. Mixins talk only to this. */
public final class Battleplacks {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Battleplacks INSTANCE =
            new Battleplacks(new CobblemonBattleSource(), new BattleFilter(), new HudRenderer());

    private final BattleSource source;
    private final BattleFilter filter;
    private final HudRenderer renderer;
    private final BattleClock clock = new BattleClock();
    private String lastLogged = "";

    Battleplacks(BattleSource source, BattleFilter filter, HudRenderer renderer) {
        this.source = source;
        this.filter = filter;
        this.renderer = renderer;
    }

    public static Battleplacks get() { return INSTANCE; }

    /** Draws the cards for the current battle, if it is one we handle. */
    public void render(GuiGraphics graphics, float partialTick) {
        PlaqueConfig cfg = PlaqueConfig.get();
        handledBattle(cfg).ifPresent(models -> renderer.render(graphics, models, cfg, partialTick, clock.elapsedSeconds(source.battleId())));
    }

    /** True while Cobblemon's own battle tiles should be suppressed (only for battles we draw). */
    public boolean replacesDefaultTiles() {
        PlaqueConfig cfg = PlaqueConfig.get();
        return cfg.showTiles && handledBattle(cfg).isPresent();
    }

    private Optional<List<PlaqueModel>> handledBattle(PlaqueConfig cfg) {
        if (!cfg.enabled || !source.inBattle()) return Optional.empty();
        List<PlaqueModel> models = source.snapshot();
        boolean accepted = filter.accepts(models, cfg);
        logDecision(models, accepted);
        return accepted ? Optional.of(models) : Optional.empty();
    }

    /** One log line each time the set of opponents or the decision changes, so "why no cards?" is answerable. */
    private void logDecision(List<PlaqueModel> models, boolean accepted) {
        StringBuilder opponents = new StringBuilder();
        for (PlaqueModel m : models) {
            if (!m.own()) opponents.append(m.kind()).append(" '").append(m.name()).append("' ");
        }
        String line = (accepted ? "showing cards" : "keeping Cobblemon's default tiles") + " vs " + opponents.toString().trim();
        if (!line.equals(lastLogged)) {
            lastLogged = line;
            LOGGER.info("[Battleplacks] {}", line);
        }
    }
}
