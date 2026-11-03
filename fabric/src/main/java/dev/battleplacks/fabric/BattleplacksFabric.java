package dev.battleplacks.fabric;

import dev.battleplacks.team.PayloadSender;
import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNameRelay;
import dev.battleplacks.team.TeamPreviewPayload;
import dev.battleplacks.team.TeamPreviewRelay;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Runs on servers (and the integrated server): relays team names, and teams players chose to share, between
 * players who have the mod. Without this on the server, nothing is shared.
 */
public final class BattleplacksFabric implements ModInitializer {
    private static final PayloadSender SENDER = (to, payload) -> {
        if (ServerPlayNetworking.canSend(to, payload.type())) ServerPlayNetworking.send(to, payload);
    };

    private final TeamNameRelay names = new TeamNameRelay(SENDER);
    private final TeamPreviewRelay teams = new TeamPreviewRelay(SENDER);

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(TeamNamePayload.TYPE, TeamNamePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TeamNamePayload.TYPE, TeamNamePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(TeamPreviewPayload.TYPE, TeamPreviewPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TeamPreviewPayload.TYPE, TeamPreviewPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TeamNamePayload.TYPE,
                (payload, context) -> names.received(context.player(), payload));
        ServerPlayNetworking.registerGlobalReceiver(TeamPreviewPayload.TYPE,
                (payload, context) -> teams.received(context.player(), payload));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> names.joined(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> names.left(handler.player));
    }
}
