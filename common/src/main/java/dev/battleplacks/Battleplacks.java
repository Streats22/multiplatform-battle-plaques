package dev.battleplacks;

import dev.battleplacks.client.BattleFilter;
import dev.battleplacks.client.BattleSource;
import dev.battleplacks.client.BattleClock;
import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.client.cobblemon.CobblemonBattleSource;
import dev.battleplacks.client.render.HudRenderer;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.team.ClientSender;
import dev.battleplacks.team.SharedTeams;
import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamPreviewPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    private ClientSender sender;
    /** The battle we last shared our team for, so it is sent once per battle. */
    private UUID sharedFor;
    private Set<UUID> shownOpponents = Set.of();

    Battleplacks(BattleSource source, BattleFilter filter, HudRenderer renderer) {
        this.source = source;
        this.filter = filter;
        this.renderer = renderer;
    }

    public static Battleplacks get() { return INSTANCE; }

    /** Set by the loader entry point once it can send packets. */
    public void setSender(ClientSender sender) { this.sender = sender; }

    /** Tells the server (and so other players) our current team name. Safe to call when there is no server mod. */
    public void announceTeamName() {
        var player = Minecraft.getInstance().player;
        if (sender != null && player != null) sender.send(new TeamNamePayload(player.getUUID(), PlaqueConfig.get().teamName));
    }

    /** Once per battle, if the player opted in: send our team so opponents with the mod can show it. */
    private void shareTeamOnce(PlaqueConfig cfg) {
        Optional<UUID> battle = source.battleId();
        if (!cfg.shareTeam || sender == null || battle.isEmpty() || battle.get().equals(sharedFor)) return;
        var player = Minecraft.getInstance().player;
        List<SharedTeams.Member> team = source.ownTeamToShare();
        if (player == null || team.isEmpty()) return; // our team arrives a moment after the battle starts; try again
        sender.send(new TeamPreviewPayload(player.getUUID(), battle.get(), team));
        sharedFor = battle.get();
    }

    /** Draws the cards for the current battle, if it is one we handle. */
    public void render(GuiGraphics graphics, float partialTick) {
        PlaqueConfig cfg = PlaqueConfig.get();
        shareTeamOnce(cfg);
        Optional<List<PlaqueModel>> battle = handledBattle(cfg);
        Set<UUID> opponents = new HashSet<>();
        battle.ifPresent(models -> {
            for (PlaqueModel m : models) if (!m.own() && m.entityId() != null) opponents.add(m.entityId());
            renderer.render(graphics, models, cfg, partialTick, clock.elapsedSeconds(source.battleId()));
        });
        shownOpponents = opponents;
    }

    /**
     * True for an opponent the overlay drew last frame, while hiding name tags is on. Read by EntityRendererMixin for
     * every name tag, so it only looks up a set; the world is drawn before the HUD, hence "last frame".
     */
    public boolean hidesNameTag(UUID entity) {
        return PlaqueConfig.get().hideOpponentNameTag && shownOpponents.contains(entity);
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
