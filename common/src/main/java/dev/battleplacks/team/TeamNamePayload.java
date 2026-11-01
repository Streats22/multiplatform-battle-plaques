package dev.battleplacks.team;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/** A player's team name. Sent client -> server (the server uses the sender's own id) and relayed server -> clients. */
public record TeamNamePayload(UUID player, String name) implements CustomPacketPayload {
    public static final Type<TeamNamePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("battleplacks", "team_name"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeamNamePayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TeamNamePayload::player,
            ByteBufCodecs.stringUtf8(TeamNameRules.MAX_LENGTH * 4), TeamNamePayload::name,
            TeamNamePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
