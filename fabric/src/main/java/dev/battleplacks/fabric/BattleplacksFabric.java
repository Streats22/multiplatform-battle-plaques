package dev.battleplacks.fabric;

import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNameRelay;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Runs on servers (and the integrated server): relays team names between players who have the mod.
 * Without this on the server, names are simply not shared.
 */
public final class BattleplacksFabric implements ModInitializer {
    private final TeamNameRelay relay = new TeamNameRelay((to, payload) -> {
        if (ServerPlayNetworking.canSend(to, TeamNamePayload.TYPE)) ServerPlayNetworking.send(to, payload);
    });

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(TeamNamePayload.TYPE, TeamNamePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TeamNamePayload.TYPE, TeamNamePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TeamNamePayload.TYPE,
                (payload, context) -> relay.received(context.player(), payload));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> relay.joined(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> relay.left(handler.player));
    }
}
