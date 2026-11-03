package dev.battleplacks.neoforge;

import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNameRelay;
import dev.battleplacks.team.TeamNames;
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
 * Runs on servers (and the integrated server): relays team names between players who have the mod.
 * The HUD itself lives in the shared client mixins in :common; the client-only wiring is in {@link BattleplacksNeoForgeClient}.
 */
@Mod("battleplacks")
public final class BattleplacksNeoForge {
    private final TeamNameRelay relay = new TeamNameRelay((to, payload) -> {
        if (to.connection.hasChannel(TeamNamePayload.TYPE)) PacketDistributor.sendToPlayer(to, payload);
    });

    public BattleplacksNeoForge(IEventBus modBus, ModContainer container, Dist dist) {
        modBus.addListener(RegisterPayloadHandlersEvent.class, this::registerPayloads);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) relay.joined(player);
        });
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) relay.left(player);
        });
        if (dist.isClient()) BattleplacksNeoForgeClient.init(container);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        // Optional, so players with the mod can still join servers without it (and the other way round)
        event.registrar("1").optional().playBidirectional(TeamNamePayload.TYPE, TeamNamePayload.CODEC,
                new DirectionalPayloadHandler<>(
                        (payload, context) -> TeamNames.set(payload.player(), payload.name()),
                        (payload, context) -> relay.received((ServerPlayer) context.player(), payload)));
    }
}
