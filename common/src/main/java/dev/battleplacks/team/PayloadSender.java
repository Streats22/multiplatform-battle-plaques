package dev.battleplacks.team;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Server side: sends a payload to one player, but only if that player's client has the mod. Implemented per loader. */
public interface PayloadSender {
    void send(ServerPlayer to, CustomPacketPayload payload);
}
