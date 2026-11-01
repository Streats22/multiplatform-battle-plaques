package dev.battleplacks.team;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * A player's appearance. Sent client -> server (the server uses the sender's own id, never {@code player}) and relayed
 * server -> clients. {@code clear} tells receivers to forget the player (they left). Fields are raw on purpose:
 * everything goes through {@link AppearanceRules} on both ends.
 */
public record AppearancePayload(UUID player, boolean clear, String name, int accentRgb, int styleId)
        implements CustomPacketPayload {
    public static final Type<AppearancePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("battleplacks", "appearance"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AppearancePayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AppearancePayload::player,
            ByteBufCodecs.BOOL, AppearancePayload::clear,
            ByteBufCodecs.stringUtf8(TeamNameRules.MAX_LENGTH * 4), AppearancePayload::name,
            ByteBufCodecs.VAR_INT, AppearancePayload::accentRgb,
            ByteBufCodecs.VAR_INT, AppearancePayload::styleId,
            AppearancePayload::new);

    public static AppearancePayload of(UUID player, Appearance a) {
        return new AppearancePayload(player, false, a.name(), a.accentRgb(), a.style().id());
    }

    public static AppearancePayload cleared(UUID player) {
        return new AppearancePayload(player, true, "", Appearance.NO_ACCENT, AppearanceRules.DEFAULT_STYLE.id());
    }

    /** The validated appearance this payload carries. */
    public Appearance appearance() { return AppearanceRules.sanitize(name, accentRgb, styleId); }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
