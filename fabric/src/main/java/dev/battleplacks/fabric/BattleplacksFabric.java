package dev.battleplacks.fabric;

import dev.battleplacks.team.Appearance;
import dev.battleplacks.team.AppearancePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runs on servers (and the integrated server): validates each player's appearance (team name, accent colour, bar
 * style) with the same rules the client uses, then relays it to every player who has the mod. Without this on the
 * server, appearances are simply not shared.
 */
public final class BattleplacksFabric implements ModInitializer {
    private final Map<UUID, Appearance> appearances = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(AppearancePayload.TYPE, AppearancePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AppearancePayload.TYPE, AppearancePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(AppearancePayload.TYPE, (payload, context) -> {
            // The sender's own id is used, never the one in the packet, so nobody can restyle someone else.
            UUID sender = context.player().getUUID();
            if (payload.clear()) forget(context.server(), sender);
            else update(context.server(), sender, payload.appearance());
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                appearances.forEach((id, look) -> send(handler.player, AppearancePayload.of(id, look))));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                forget(server, handler.player.getUUID()));
    }

    private void update(MinecraftServer server, UUID player, Appearance look) {
        appearances.put(player, look);
        broadcast(server, AppearancePayload.of(player, look));
    }

    private void forget(MinecraftServer server, UUID player) {
        appearances.remove(player);
        broadcast(server, AppearancePayload.cleared(player));
    }

    private static void broadcast(MinecraftServer server, AppearancePayload payload) {
        for (ServerPlayer other : server.getPlayerList().getPlayers()) send(other, payload);
    }

    private static void send(ServerPlayer to, AppearancePayload payload) {
        if (ServerPlayNetworking.canSend(to, AppearancePayload.TYPE)) ServerPlayNetworking.send(to, payload);
    }
}
