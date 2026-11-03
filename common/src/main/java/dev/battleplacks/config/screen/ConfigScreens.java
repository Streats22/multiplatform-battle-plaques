package dev.battleplacks.config.screen;

import dev.battleplacks.Battleplacks;
import dev.battleplacks.config.Anchor;
import dev.battleplacks.config.ArmPose;
import dev.battleplacks.config.HeadPose;
import dev.battleplacks.config.TeamLayout;
import dev.battleplacks.client.cobblemon.PartyList;
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
import java.util.List;
import java.util.Optional;

/**
 * The in-game settings screen (Cloth Config), opened from Mod Menu on Fabric and the mod list on NeoForge. Defaults
 * come from a fresh {@link PlaqueConfig}, so "reset" always matches the code. Saving writes config/battleplacks.json.
 * Every tooltip should tell a player what they will see change, in plain words; "\n" starts a new tooltip line.
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
        battles(page(builder, "Which battles"), cfg, d);
        trainer(page(builder, "Trainer"), cfg, d);
        pokemon(page(builder, "Pokémon"), cfg, d);
        position(page(builder, "Position"), cfg, d);
        show(page(builder, "Show / hide"), cfg, d);
        look(page(builder, "Colours"), cfg, d);
        timing(page(builder, "Timing"), cfg, d);
        return builder.build();
    }

    private static ConfigPage page(ConfigBuilder builder, String name) {
        return new ConfigPage(builder.getOrCreateCategory(Component.literal(name)), builder.entryBuilder());
    }

    private static void general(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.toggle("Enabled", "Turns Battleplacks on or off. Off shows Cobblemon's normal battle tiles only.",
                 c.enabled, d.enabled, v -> c.enabled = v)
         .text("Team name", "The name shown under your trainer, also to other players who have the mod."
                 + "\nLeave empty to show \"Team <your name>\". Max 20 characters, no swear words.",
                 c.teamName, ConfigScreens::teamNameError, v -> c.teamName = TeamNameRules.clean(v));
    }

    private static Optional<Component> teamNameError(String text) {
        TeamNameRules.Result result = TeamNameRules.validate(text);
        return result.valid() ? Optional.empty() : Optional.of(Component.literal(result.reason()));
    }

    private static void battles(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.toggle("Player battles", "Show the overlay when you battle another player.",
                 c.includePlayerBattles, d.includePlayerBattles, v -> c.includePlayerBattles = v)
         .toggle("Wild battles", "Show the overlay against wild Pokémon too.",
                 c.includeWildBattles, d.includeWildBattles, v -> c.includeWildBattles = v)
         .choice("NPC trainers", "Which NPC trainers (like Radical Cobblemon Trainers) get the overlay:"
                 + "\nAll: every NPC trainer."
                 + "\nOnly matching: only trainers whose name matches the list below."
                 + "\nExcept matching: every trainer except those on the list.",
                 TrainerFilterMode.class, c.trainerFilterMode, d.trainerFilterMode, v -> c.trainerFilterMode = v)
         .strings("Trainer names", "Used by \"Only matching\" and \"Except matching\"."
                 + "\nA trainer matches when its name contains one of these (upper/lower case doesn't matter),"
                 + "\ne.g. \"Leader\" matches \"Gym Leader Brock\".",
                 c.trainerPatterns, d.trainerPatterns, v -> c.trainerPatterns = new ArrayList<>(v));
    }

    private static final String STANCES = "Standing: turned a little towards the middle of the screen."
            + "\nLeaning: leaning forward into the battle."
            + "\nHero: almost facing you, slight lean."
            + "\nCrouching: sneaking pose."
            + "\nProfile: seen from the side."
            + "\nOver shoulder: turned away, looking back."
            + "\nFront: facing you straight on."
            + "\nCustom: set your own turn and lean below.";

    private static final String ARMS = "Natural: the game's own arm movement."
            + "\nWave: one arm up, waving."
            + "\nPoint: pointing at the opponent."
            + "\nHands on hips: arms out to the sides."
            + "\nArms crossed: arms folded in front."
            + "\nCheer: both arms up."
            + "\nReady to throw: arm raised, about to throw a Poké Ball.";

    private static final String HEADS = "The face itself is part of the skin and can't change, but the head can:"
            + "\nStraight: looking ahead."
            + "\nConfident: chin up."
            + "\nCurious: head tilted to the side."
            + "\nDetermined: chin down, staring ahead."
            + "\nCocky: chin up with a tilt.";

    private static void trainer(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.choice("Your pose", "How your own trainer stands.\n" + STANCES,
                 Stance.class, c.ownStance, d.ownStance, v -> c.ownStance = v)
         .choice("Opponent pose", "How the opponent's trainer stands.\n" + STANCES,
                 Stance.class, c.opponentStance, d.opponentStance, v -> c.opponentStance = v)
         .slider("Custom turn", "Only for the Custom pose: how far the trainer turns towards the middle of the screen."
                 + "\n0° faces you, 90° is side-on.", c.customFacing, d.customFacing, -180, 180, "°", v -> c.customFacing = v)
         .slider("Custom lean", "Only for the Custom pose: how far the trainer leans forward. Negative leans back.",
                 c.customLean, d.customLean, -30, 40, "°", v -> c.customLean = v)
         .choice("Your arms", "What your trainer does with their arms.\n" + ARMS,
                 ArmPose.class, c.ownArms, d.ownArms, v -> c.ownArms = v)
         .choice("Opponent arms", "What the opponent's trainer does with their arms.\n" + ARMS,
                 ArmPose.class, c.opponentArms, d.opponentArms, v -> c.opponentArms = v)
         .choice("Your head", "How your trainer holds their head.\n" + HEADS,
                 HeadPose.class, c.ownHead, d.ownHead, v -> c.ownHead = v)
         .choice("Opponent head", "How the opponent's trainer holds their head.\n" + HEADS,
                 HeadPose.class, c.opponentHead, d.opponentHead, v -> c.opponentHead = v)
         .slider("Trainer size", "Size of the trainers. Bigger shows less of the body, since the overlay cuts them off at the bottom.",
                 c.trainerSize, d.trainerSize, 50, 200, "%", v -> c.trainerSize = v)
         .slider("Trainer height", "Moves the trainers down (positive) or up (negative). At 0 the head touches the top.",
                 c.trainerOffset, d.trainerOffset, -30, 30, " px", v -> c.trainerOffset = v);
    }

    private static void pokemon(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.choice("Team layout", "Which Pokémon show next to the trainer:"
                 + "\nActive only: just the Pokémon in battle."
                 + "\nActive and bench: the one in battle big, the rest of the team small beside it."
                 + "\nFull team: the whole team the same size, the one in battle marked with a line."
                 + "\nMy picks: the Pokémon you tick below (the opponent's side shows active and bench)."
                 + "\nAn opponent's Pokémon only show once they have been sent out, unless they share their team.",
                 TeamLayout.class, c.teamLayout, d.teamLayout, v -> c.teamLayout = v);
        myPicks(p, c);
        p.toggle("Show my team to opponents", "Lets opponents who also have Battleplacks see your whole team from the start."
                 + "\nOff: they only see the Pokémon you send out. Takes effect from the next battle.",
                 c.shareTeam, d.shareTeam, v -> c.shareTeam = v)
         .slider("Pokémon size", "Size of the Pokémon next to the trainer. 100% is about Cobblemon's own portrait.",
                 c.pokemonSize, d.pokemonSize, 50, 200, "%", v -> c.pokemonSize = v)
         .slider("Pokémon height", "Moves the Pokémon down (positive) or up (negative). At 0 its top touches the top.",
                 c.pokemonOffset, d.pokemonOffset, -30, 30, " px", v -> c.pokemonOffset = v)
         .slider("Pokémon sideways", "Moves the Pokémon towards the middle of the screen (positive) or towards its trainer (negative)."
                 + "\nBoth sides move mirrored, so they stay symmetrical.",
                 c.pokemonSideways, d.pokemonSideways, -30, 60, " px", v -> c.pokemonSideways = v)
         .toggle("Animate Pokémon", "On: the Pokémon move (idle animation). Off: a still pose, like a screenshot.",
                 c.animatePokemon, d.animatePokemon, v -> c.animatePokemon = v);
    }

    /** One tick box per Pokémon in your party right now, for the "My picks" layout. Saved by Pokémon id. */
    private static void myPicks(ConfigPage p, PlaqueConfig c) {
        List<PartyList.Entry> party;
        try {
            party = PartyList.current();
        } catch (RuntimeException e) {
            party = List.of(); // not in a world yet
        }
        if (party.isEmpty()) {
            p.note("My picks: join a world to choose from your party.");
            return;
        }
        for (PartyList.Entry entry : party) {
            String id = entry.id().toString();
            p.toggle("Pick: " + entry.label(), "Show this Pokémon when \"Team layout\" is \"My picks\".",
                     c.pickedPokemon.contains(id), false, v -> {
                         c.pickedPokemon.remove(id);
                         if (v) c.pickedPokemon.add(id);
                     });
        }
    }

    private static void position(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.choice("Your corner", "Which corner of the screen your side of the overlay sits in.",
                 Anchor.class, c.ownAnchor, d.ownAnchor, v -> c.ownAnchor = v)
         .choice("Opponent corner", "Which corner of the screen the opponent's side sits in.",
                 Anchor.class, c.opponentAnchor, d.opponentAnchor, v -> c.opponentAnchor = v)
         .slider("Overall size", "Makes the whole overlay bigger or smaller.",
                 Math.round(c.scale * 100), Math.round(d.scale * 100), 50, 200, "%", v -> c.scale = v / 100f)
         .slider("Distance from edge", "Space between the overlay and the edge of the screen.",
                 c.margin, d.margin, 0, 40, " px", v -> c.margin = v)
         .slider("Gap under Cobblemon tile", "Extra space between Cobblemon's own Pokémon tile and the overlay underneath it.",
                 c.stripOffset, d.stripOffset, -20, 60, " px", v -> c.stripOffset = v);
    }

    private static void show(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.toggle("Trainer and Pokémon", "The picture of the trainer with their Pokémon.",
                 c.showHead, d.showHead, v -> c.showHead = v)
         .toggle("Party bar", "The bar showing how many Pokémon each side has left.",
                 c.showParty, d.showParty, v -> c.showParty = v)
         .choice("Party bar style", "Pips: a segmented bar, like Cobblemon's. Balls: a row of Poké Balls.",
                 BallStyle.class, c.ballStyle, d.ballStyle, v -> c.ballStyle = v)
         .toggle("Unseen opponent Pokémon", "Show the opponent's Pokémon you haven't seen yet as grey slots."
                 + "\nNever shows which Pokémon they are.",
                 c.showUnknownSlots, d.showUnknownSlots, v -> c.showUnknownSlots = v)
         .toggle("Team name", "The team or trainer name under the party bar.",
                 c.showTrainerName, d.showTrainerName, v -> c.showTrainerName = v)
         .toggle("Hide opponent's name tag", "Hides the floating name above the opponent in the world during the battle."
                 + "\nThe overlay already shows their name, and an NPC standing right in front of you"
                 + "\nputs its name tag in the middle of your screen.",
                 c.hideOpponentNameTag, d.hideOpponentNameTag, v -> c.hideOpponentNameTag = v)
         .toggle("Background panel", "A frame behind the trainer and Pokémon. A resource pack can replace its look.",
                 c.showStripBackground, d.showStripBackground, v -> c.showStripBackground = v)
         .toggle("Replace Cobblemon tiles", "Off: keep Cobblemon's own Pokémon tile and put the overlay under it."
                 + "\nOn: hide Cobblemon's tile and draw Battleplacks' own tile (with the options below) instead.",
                 c.showTiles, d.showTiles, v -> c.showTiles = v)
         .toggle("Tile: level", "Only with \"Replace Cobblemon tiles\": show the Pokémon's level.",
                 c.showLevel, d.showLevel, v -> c.showLevel = v)
         .toggle("Tile: HP numbers", "Only with \"Replace Cobblemon tiles\": show HP as numbers on the bar.",
                 c.showHpText, d.showHpText, v -> c.showHpText = v)
         .toggle("Tile: status", "Only with \"Replace Cobblemon tiles\": show status like poison or sleep.",
                 c.showStatus, d.showStatus, v -> c.showStatus = v);
    }

    private static void look(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.choice("Theme", "Colour scheme: the party bar colours, and Battleplacks' own tiles"
                 + "\n(see \"Replace Cobblemon tiles\" under Show / hide).",
                 Theme.class, c.theme, d.theme, v -> c.theme = v)
         .slider("Tile opacity", "Only with \"Replace Cobblemon tiles\": how solid the tile background is."
                 + "\nLower lets more of the world show through.",
                 c.opacity, d.opacity, 20, 100, "%", v -> c.opacity = v)
         .colour("Your colour", "Colour of your party bar (and your side of the VS intro)."
                 + "\nFully transparent (#00000000) uses the theme's colour.", c.accentOwn, v -> c.accentOwn = v)
         .colour("Opponent colour", "Colour of the opponent's party bar (and their side of the VS intro)."
                 + "\nFully transparent (#00000000) uses the theme's colour.", c.accentOpponent, v -> c.accentOpponent = v);
    }

    private static void timing(ConfigPage p, PlaqueConfig c, PlaqueConfig d) {
        p.toggle("Hide after a while", "Off: the overlay stays for the whole battle."
                 + "\nOn: it slides away after the time below.",
                 c.stripFades, d.stripFades, v -> c.stripFades = v)
         .slider("Hide after", "Only with \"Hide after a while\": seconds before the overlay slides away.",
                 c.stripSeconds, d.stripSeconds, 1, 15, " s", v -> c.stripSeconds = v)
         .toggle("Battle intro", "A short full-screen \"VS\" presentation of both trainers when a battle starts.",
                 c.introEnabled, d.introEnabled, v -> c.introEnabled = v)
         .slider("Intro length", "How long the VS intro stays on screen.",
                 c.introSeconds, d.introSeconds, 1, 8, " s", v -> c.introSeconds = v);
    }
}
