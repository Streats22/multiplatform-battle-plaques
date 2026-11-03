package dev.battleplacks.neoforge;

import dev.battleplacks.team.PayloadSender;
import dev.battleplacks.team.SharedTeams;
import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNameRelay;
import dev.battleplacks.team.TeamNames;
import dev.battleplacks.team.TeamPreviewPayload;
import dev.battleplacks.team.TeamPreviewRelay;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

/**
 * Runs on servers (and the integrated server): relays team names, and teams players chose to share, between players
 * who have the mod. The HUD itself lives in the shared client mixins in :common; the client-only wiring is in
 * {@link BattleplacksNeoForgeClient}.
 */
@Mod("battleplacks")
public final class BattleplacksNeoForge {
    private static final PayloadSender SENDER = (to, payload) -> {
        if (to.connection.hasChannel(payload.type())) PacketDistributor.sendToPlayer(to, payload);
    };

    private final TeamNameRelay names = new TeamNameRelay(SENDER);
    private final TeamPreviewRelay teams = new TeamPreviewRelay(SENDER);

    public BattleplacksNeoForge(IEventBus modBus, ModContainer container, Dist dist) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) names.joined(player);
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) names.left(player);
        });
        if (dist.isClient()) BattleplacksNeoForgeClient.init(container);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        // Optional, so players with the mod can still join servers without it (and the other way round)
        var registrar = event.registrar("1").optional();
        registrar.playBidirectional(TeamNamePayload.TYPE, TeamNamePayload.CODEC,
                new DirectionalPayloadHandler<>(
                        (payload, context) -> TeamNames.set(payload.player(), payload.name()),
                        (payload, context) -> names.received((ServerPlayer) context.player(), payload)));
        registrar.playBidirectional(TeamPreviewPayload.TYPE, TeamPreviewPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        (payload, context) -> SharedTeams.set(payload.player(), payload.battle(), payload.team()),
                        (payload, context) -> teams.received((ServerPlayer) context.player(), payload)));
    }
}
