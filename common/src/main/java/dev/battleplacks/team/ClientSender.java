package dev.battleplacks.team;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client side: sends a payload to the server, but only if the server has the mod (otherwise does nothing).
 * Implemented per loader; used for the team name and the shared team.
 */
public interface ClientSender {
    void send(CustomPacketPayload payload);
}
