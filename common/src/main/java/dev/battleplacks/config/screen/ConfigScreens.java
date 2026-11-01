package dev.battleplacks.config.screen;

import dev.battleplacks.Battleplacks;
import dev.battleplacks.config.Anchor;
import dev.battleplacks.config.BallStyle;
import dev.battleplacks.config.PlaqueConfig;
import dev.battleplacks.config.Stance;
import dev.battleplacks.config.Theme;
import dev.battleplacks.config.TrainerFilterMode;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import dev.battleplacks.team.TeamNameRules;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The in-game settings screen (Cloth Config), opened from Mod Menu on Fabric. Defaults come from a fresh
 * {@link PlaqueConfig}, so "reset" always matches the code. Saving writes config/battleplacks.json.
 */
public final class ConfigScreens {
    private ConfigScreens() {}

    public static Screen create(Screen parent) {
        PlaqueConfig cfg = PlaqueConfig.get();
        PlaqueConfig d = new PlaqueConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Cobblemon - Battleplacks"))
                .setSavingRunnable(() -> {
                    cfg.save();
                    Battleplacks.get().announceTeamName();
                });

        general(page(builder, "General"), cfg, d);
        trainers(page(builder, "Trainers"), cfg, d);
        layout(page(builder, "Layout"), cfg, d);
        intro(page(builder, "Timing"), cfg, d);
        look(page(builder, "Look"), cfg, d);
        elements(page(builder, "Elements"), cfg, d);
        return builder.build();
    }

    private static ConfigPage page(ConfigBuilder builder, String name) {
        return new ConfigPage(builder.getOrCreateCategory(Component.literal(name)), builder.entryBuilder());
    }

    private static void general(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.text("Team name", "Shown on your card, and to other players who have the mod. Max 20 characters, no profanity.",
                 c.teamName, ConfigScreens::teamNameError, v -> c.teamName = TeamNameRules.clean(v))
         .toggle("Enabled", "Turn Battleplacks on or off.", c.enabled, d.enabled, v -> c.enabled = v)
         .toggle("Own Pokemon tiles", "Draw Battleplacks' own Pokemon tile INSTEAD of Cobblemon's. Off keeps Cobblemon's tile and puts the strip under it.",
                 c.showTiles, d.showTiles, v -> c.showTiles = v)
         .toggle("Player battles", "Show cards when battling other players.",
                 c.includePlayerBattles, d.includePlayerBattles, v -> c.includePlayerBattles = v)
         .toggle("Wild battles", "Show cards against wild Pokemon.",
                 c.includeWildBattles, d.includeWildBattles, v -> c.includeWildBattles = v);
    }

    private static Optional<net.minecraft.network.chat.Component> teamNameError(String text) {
        TeamNameRules.Result result = TeamNameRules.validate(text);
        return result.valid() ? Optional.empty() : Optional.of(net.minecraft.network.chat.Component.literal(result.reason()));
    }

    private static void trainers(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.choice("Trainer filter", "ALL: every NPC trainer. ONLY_MATCHING: only names matching the list. EXCEPT_MATCHING: all but those.",
                 TrainerFilterMode.class, c.trainerFilterMode, d.trainerFilterMode, v -> c.trainerFilterMode = v)
         .strings("Name patterns", "Case-insensitive; plain text or regex, tested against the trainer's display name.",
                 c.trainerPatterns, d.trainerPatterns, v -> c.trainerPatterns = new ArrayList<>(v));
    }

    private static void layout(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.choice("Your corner", "Where your card sits.", Anchor.class, c.ownAnchor, d.ownAnchor, v -> c.ownAnchor = v)
         .choice("Opponent corner", "Where the opponent's card sits.", Anchor.class, c.opponentAnchor, d.opponentAnchor, v -> c.opponentAnchor = v)
         .choice("Your stance", "Pose of your 3D player.", Stance.class, c.ownStance, d.ownStance, v -> c.ownStance = v)
         .choice("Opponent stance", "Pose of the opponent's 3D trainer.", Stance.class, c.opponentStance, d.opponentStance, v -> c.opponentStance = v)
         .slider("Custom facing", "For the CUSTOM stance: degrees turned towards the screen centre.", c.customFacing, d.customFacing, -180, 180, "\u00b0", v -> c.customFacing = v)
         .slider("Custom lean", "For the CUSTOM stance: degrees leaned forward.", c.customLean, d.customLean, -30, 40, "\u00b0", v -> c.customLean = v)
         .toggle("Strip background", "Draw the strip background texture (replaceable by a resource pack).", c.showStripBackground, d.showStripBackground, v -> c.showStripBackground = v)
         .slider("Scale", "Size of the cards.", Math.round(c.scale * 100), Math.round(d.scale * 100), 50, 200, "%", v -> c.scale = v / 100f)
         .slider("Margin", "Distance from the screen edge, in GUI pixels.", c.margin, d.margin, 0, 40, " px", v -> c.margin = v)
         .slider("Offset under tile", "Extra space between Cobblemon's tile and the strip.", c.stripOffset, d.stripOffset, -20, 60, " px", v -> c.stripOffset = v)
         .toggle("Compact", "Smaller cards.", c.compact, d.compact, v -> c.compact = v);
    }

    private static void intro(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.toggle("Fade strip", "Make the strip fade away after a few seconds instead of staying on screen.",
                 c.stripFades, d.stripFades, v -> c.stripFades = v)
         .slider("Strip visible for", "Seconds the strip stays before it fades.", c.stripSeconds, d.stripSeconds, 1, 15, " s", v -> c.stripSeconds = v)
         .toggle("Battle intro", "Show a short presentation of both trainers when a battle starts.",
                 c.introEnabled, d.introEnabled, v -> c.introEnabled = v)
         .slider("Intro length", "How long the intro stays on screen.", c.introSeconds, d.introSeconds, 1, 8, " s", v -> c.introSeconds = v);
    }

    private static void look(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.choice("Theme", "Colour scheme.", Theme.class, c.theme, d.theme, v -> c.theme = v)
         .choice("Party style", "PIPS is the segmented bar, BALLS draws poke balls.", BallStyle.class, c.ballStyle, d.ballStyle, v -> c.ballStyle = v)
         .toggle("Animate Pokemon", "Off shows a still pose of the Pokemon.", c.animatePokemon, d.animatePokemon, v -> c.animatePokemon = v)
         .slider("Opacity", "Panel opacity.", c.opacity, d.opacity, 20, 100, "%", v -> c.opacity = v)
         .colour("Your accent", "Override the accent colour on your card (0 = theme colour).", c.accentOwn, v -> c.accentOwn = v)
         .colour("Opponent accent", "Override the accent colour on the opponent's card (0 = theme colour).", c.accentOpponent, v -> c.accentOpponent = v);
    }

    private static void elements(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.toggle("3D trainer", "Show the trainer model.", c.showHead, d.showHead, v -> c.showHead = v)
         .toggle("Team name", "Show the team label.", c.showTrainerName, d.showTrainerName, v -> c.showTrainerName = v)
         .toggle("Party", "Show the party bar.", c.showParty, d.showParty, v -> c.showParty = v)
         .toggle("Unknown slots", "Show hidden opponent slots as grey placeholders.", c.showUnknownSlots, d.showUnknownSlots, v -> c.showUnknownSlots = v)
         .toggle("Level", "Show Pokemon levels.", c.showLevel, d.showLevel, v -> c.showLevel = v)
         .toggle("HP text", "Show HP numbers on the bar.", c.showHpText, d.showHpText, v -> c.showHpText = v)
         .toggle("Status", "Show status conditions.", c.showStatus, d.showStatus, v -> c.showStatus = v);
    }
}
