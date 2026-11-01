package dev.battleplacks.fabric;

import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNameRules;
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
 * Runs on servers (and the integrated server): validates each player's team name with the same rules the client
 * uses, then relays it to every player who has the mod. Without this on the server, names are simply not shared.
 */
public final class BattleplacksFabric implements ModInitializer {
    private final Map<UUID, String> names = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(TeamNamePayload.TYPE, TeamNamePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TeamNamePayload.TYPE, TeamNamePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TeamNamePayload.TYPE, (payload, context) -> {
            // The sender's own id is used, never the one in the packet, so nobody can rename someone else.
            TeamNameRules.Result result = TeamNameRules.validate(payload.name());
            if (!result.valid()) return;
            update(context.server(), context.player().getUUID(), result.name());
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                names.forEach((id, name) -> send(handler.player, id, name)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                update(server, handler.player.getUUID(), ""));
    }

    private void update(MinecraftServer server, UUID player, String name) {
        if (name.isEmpty()) names.remove(player); else names.put(player, name);
        for (ServerPlayer other : server.getPlayerList().getPlayers()) send(other, player, name);
    }

    private static void send(ServerPlayer to, UUID about, String name) {
        if (ServerPlayNetworking.canSend(to, TeamNamePayload.TYPE)) {
            ServerPlayNetworking.send(to, new TeamNamePayload(about, name));
        }
    }
}
