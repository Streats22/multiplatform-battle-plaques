package dev.battleplacks.team;

import dev.battleplacks.client.PlaqueModel.SlotState;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A team a player chose to share ("Show my team to opponents"), for one battle. Sent client -> server when the
 * battle starts; the server checks the battle, fills in the sender's own id and relays it to the other players in
 * that battle only. Sizes are capped by the codec so a modified client cannot send huge packets.
 */
public record TeamPreviewPayload(UUID player, UUID battle, List<SharedTeams.Member> team) implements CustomPacketPayload {
    public static final int MAX_TEAM = 6;
    private static final int MAX_ID = 64, MAX_ASPECTS = 16;

    public static final Type<TeamPreviewPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("battleplacks", "team_preview"));

    private static final StreamCodec<RegistryFriendlyByteBuf, SlotState> STATE = ByteBufCodecs.VAR_INT.map(
            i -> SlotState.values()[Math.floorMod(i, SlotState.values().length)], SlotState::ordinal).cast();

    private static final StreamCodec<RegistryFriendlyByteBuf, Set<String>> ASPECTS =
            ByteBufCodecs.<RegistryFriendlyByteBuf, String, Set<String>>collection(HashSet::new, ByteBufCodecs.stringUtf8(MAX_ID), MAX_ASPECTS);

    private static final StreamCodec<RegistryFriendlyByteBuf, SharedTeams.Member> MEMBER = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, SharedTeams.Member::id,
            ByteBufCodecs.stringUtf8(MAX_ID), SharedTeams.Member::species,
            ASPECTS, SharedTeams.Member::aspects,
            STATE, SharedTeams.Member::state,
            SharedTeams.Member::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, TeamPreviewPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TeamPreviewPayload::player,
            UUIDUtil.STREAM_CODEC, TeamPreviewPayload::battle,
            MEMBER.apply(ByteBufCodecs.list(MAX_TEAM)), TeamPreviewPayload::team,
            TeamPreviewPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
