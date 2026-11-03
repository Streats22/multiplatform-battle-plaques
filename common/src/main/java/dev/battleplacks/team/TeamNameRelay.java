package dev.battleplacks.team;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server side of team name sharing (also runs on the integrated server): validates each player's team name with the
 * same rules the client uses, then relays it to every player who has the mod. The loader entry points only wire
 * their networking and join/leave events to this.
 */
public final class TeamNameRelay {
    /** Sends a payload to one player, but only if that player's client has the mod. Implemented per loader. */
    public interface Sender {
        void send(ServerPlayer to, TeamNamePayload payload);
    }

    private final Map<UUID, String> names = new ConcurrentHashMap<>();
    private final Sender sender;

    public TeamNameRelay(Sender sender) { this.sender = sender; }

    /** The sender's own id is used, never the one in the packet, so nobody can rename someone else. */
    public void received(ServerPlayer from, TeamNamePayload payload) {
        TeamNameRules.Result result = TeamNameRules.validate(payload.name());
        if (!result.valid()) return;
        update(from.server, from.getUUID(), result.name());
    }

    public void joined(ServerPlayer player) {
        names.forEach((id, name) -> sender.send(player, new TeamNamePayload(id, name)));
    }

    public void left(ServerPlayer player) {
        update(player.server, player.getUUID(), "");
    }

    private void update(MinecraftServer server, UUID player, String name) {
        if (name.isEmpty()) names.remove(player); else names.put(player, name);
        for (ServerPlayer other : server.getPlayerList().getPlayers()) sender.send(other, new TeamNamePayload(player, name));
    }
}
