package dev.battleplacks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.battleplacks.team.Appearance;
import dev.battleplacks.team.AppearanceRules;
import dev.battleplacks.team.TeamNameRules;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/** User settings, stored as config/battleplacks.json. Every field is mirrored in preview/index.html. */
public class PlaqueConfig {
    /** Your team name, shown to you and to other players with the mod. Empty uses "Team <your name>". */
    public String teamName = "";
    public boolean enabled = true;
    /** Cards for battles against other players. */
    public boolean includePlayerBattles = true;
    /** Cards for wild Pokémon battles (off by default: Cobblemon's own tiles are kept). */
    public boolean includeWildBattles = false;
    /** Which NPC trainers (RCT) get cards, matched on the trainer's display name. */
    public TrainerFilterMode trainerFilterMode = TrainerFilterMode.ONLY_MATCHING;
    /** Case-insensitive regexes (plain text works too) tested against the trainer's display name. */
    public List<String> trainerPatterns = new ArrayList<>(
            List.of("Gym Leader", "Leader", "Elite Four", "Champion", "Rival"));
    /** Draw Battleplacks' own Pokémon tile INSTEAD of Cobblemon's (off by default: the strip sits under Cobblemon's tile). */
    public boolean showTiles = false;
    /** Extra pixels between Cobblemon's tile and the strip. */
    public int stripOffset = 0;
    /** Short presentation of both trainers when a battle starts. */
    /** The strip fades away this many seconds after the battle (or the intro) starts; off keeps it on screen. */
    /** Animate the Pokémon models. Off shows a still pose, like a screenshot. */
    public boolean animatePokemon = false;
    public boolean stripFades = true;
    public int stripSeconds = 3;
    public boolean introEnabled = false;
    public int introSeconds = 4;
    public Theme theme = Theme.COBBLEMON;
    public Anchor ownAnchor = Anchor.TOP_LEFT;
    public Anchor opponentAnchor = Anchor.TOP_RIGHT;
    /** Bar style: PIPS (Cobblemon's bar with slanted pips, default), SEGMENTED (plain bar) or BALLS. Shared with other players. */
    public BallStyle ballStyle = BallStyle.PIPS;
    /** Team label above or below the party bar. */
    public LabelPosition labelPosition = LabelPosition.BELOW;
    /** Pokemon in the strip: size in % of the base size, and how many pixels it is raised (negative lowers it). */
    public int pokemonSize = 100;
    public int pokemonLift = 11;
    /** 3D trainer in the strip: size in % and pixels raised. */
    public int trainerSize = 100;
    public int trainerLift = 0;
    /** Pose of the 3D trainer on each side. */
    public Stance ownStance = Stance.LEANING;
    public Stance opponentStance = Stance.STANDING;
    /** Used when a stance is CUSTOM: degrees turned towards the screen centre, and degrees leaned forward. */
    public int customFacing = 35;
    public int customLean = 10;
    /** Draw the strip's background texture (assets/battleplacks/textures/gui/strip.png, replaceable by a resource pack). */
    public boolean showStripBackground = false;
    public float scale = 1.0f;
    public int margin = 8;
    public boolean showHead = true;
    public boolean showTrainerName = true;
    public boolean showParty = true;
    public boolean showLevel = true;
    public boolean showHpText = true;
    public boolean showStatus = true;
    /** Show opponent party slots as unknown "?" balls (never reveals hidden info). */
    public boolean showUnknownSlots = true;
    public boolean compact = false;
    /** 0-100 */
    public int opacity = 92;
    /** ARGB overrides; 0 means "use theme default". accentOwn is also the accent other players see on your card. */
    public int accentOwn = 0;
    public int accentOpponent = 0;

    /** What other players should see: team name, accent colour and bar style. Validated like everything that is shared. */
    public Appearance appearance() {
        return AppearanceRules.sanitize(teamName, AppearanceRules.accentFromConfig(accentOwn), ballStyle.id());
    }

    /** Repairs a loaded or hand-edited config: missing enums (Gson leaves them null) and out-of-range numbers. */
    public PlaqueConfig sanitize() {
        PlaqueConfig d = new PlaqueConfig();
        teamName = TeamNameRules.clean(teamName);
        if (trainerFilterMode == null) trainerFilterMode = d.trainerFilterMode;
        if (trainerPatterns == null) trainerPatterns = new ArrayList<>(d.trainerPatterns);
        if (theme == null) theme = d.theme;
        if (ownAnchor == null) ownAnchor = d.ownAnchor;
        if (opponentAnchor == null) opponentAnchor = d.opponentAnchor;
        if (ballStyle == null) ballStyle = d.ballStyle;
        if (labelPosition == null) labelPosition = d.labelPosition;
        if (ownStance == null) ownStance = d.ownStance;
        if (opponentStance == null) opponentStance = d.opponentStance;
        pokemonSize = Math.max(MIN_SIZE, Math.min(MAX_SIZE, pokemonSize));
        trainerSize = Math.max(MIN_SIZE, Math.min(MAX_SIZE, trainerSize));
        pokemonLift = Math.max(-MAX_LIFT, Math.min(MAX_LIFT, pokemonLift));
        trainerLift = Math.max(-MAX_LIFT, Math.min(MAX_LIFT, trainerLift));
        return this;
    }

    public static final int MIN_SIZE = 50, MAX_SIZE = 200, MAX_LIFT = 40;

    public Anchor anchorFor(boolean own) { return own ? ownAnchor : opponentAnchor; }

    public Stance stanceFor(boolean own) { return own ? ownStance : opponentStance; }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static PlaqueConfig instance;

    public static PlaqueConfig get() {
        if (instance == null) instance = load();
        return instance;
    }

    private static Path file() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("battleplacks.json");
    }

    public static PlaqueConfig load() {
        try {
            Path f = file();
            if (Files.exists(f)) {
                PlaqueConfig c = GSON.fromJson(Files.readString(f), PlaqueConfig.class);
                if (c != null) return c.sanitize();
            }
        } catch (Exception ignored) {
            // fall through to defaults on a missing or corrupt file
        }
        PlaqueConfig c = new PlaqueConfig();
        c.save();
        return c;
    }

    public void save() {
        try {
            Path f = file();
            Files.createDirectories(f.getParent());
            Files.writeString(f, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("[battleplacks] could not save config: " + e);
        }
    }
}
