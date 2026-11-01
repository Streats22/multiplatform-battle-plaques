package dev.battleplacks.client.render;

import dev.battleplacks.client.PlaqueModel;
import dev.battleplacks.config.BallStyle;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Theme;
import dev.battleplacks.team.Appearance;
import dev.battleplacks.team.Appearances;
import net.minecraft.client.Minecraft;

/**
 * Which accent colour and bar style one card uses. You see your own settings; another player is drawn with the
 * appearance they shared (if any); NPC trainers and players without the mod fall back to the theme and your style.
 */
record CardLook(int accent, BallStyle style, String teamName) {
    static CardLook of(PlaqueModel model, PlaqueConfig cfg, Theme.Palette palette) {
        Appearance shared = sharedFor(model, cfg);
        int accent = shared != null && shared.hasAccent() ? shared.accentArgb() : palette.accent(model.own(), cfg);
        BallStyle style = shared != null ? shared.style() : cfg.ballStyle;
        return new CardLook(accent, style, shared != null ? shared.name() : "");
    }

    /**
     * Your own settings for you, the synced appearance for other players (also a teammate on your side), nothing for
     * NPCs and wild Pokemon. "Me" is decided by UUID, because {@code model.own()} only means "on my side".
     */
    private static Appearance sharedFor(PlaqueModel model, PlaqueConfig cfg) {
        if (model.kind() != PlaqueModel.Kind.PLAYER) return null;
        var player = Minecraft.getInstance().player;
        boolean me = player != null && player.getUUID().equals(model.entityId());
        return me ? cfg.appearance() : Appearances.get(model.entityId()).orElse(null);
    }
}
