package dev.battleplacks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    /** Size of the Pokémon in the strip, in percent (100 is about Cobblemon's tile portrait). */
    public int pokemonSize = 100;
    /** Moves the Pokémon in the strip down (positive) or up (negative), in pixels. 0 puts its top at the top of the strip. */
    public int pokemonOffset = 12;
    /** Moves the Pokémon towards the middle of the screen (positive) or towards its trainer (negative), in pixels. */
    public int pokemonSideways = 14;
    /** Animate the Pokémon models. Off shows a still pose, like a screenshot. */
    public boolean animatePokemon = false;
    /** The strip fades away stripSeconds after the battle (or the intro) starts; off (default) keeps it on screen. */
    public boolean stripFades = false;
    public int stripSeconds = 3;
    /** Short presentation of both trainers when a battle starts. */
    public boolean introEnabled = false;
    public int introSeconds = 4;
    public Theme theme = Theme.COBBLEMON;
    public Anchor ownAnchor = Anchor.TOP_LEFT;
    public Anchor opponentAnchor = Anchor.TOP_RIGHT;
    /** PIPS is the segmented party bar (default); BALLS draws poke balls. */
    public BallStyle ballStyle = BallStyle.PIPS;
    /** Pose of the 3D trainer on each side. */
    public Stance ownStance = Stance.LEANING;
    public Stance opponentStance = Stance.STANDING;
    /** Used when a stance is CUSTOM: degrees turned towards the screen centre, and degrees leaned forward. */
    public int customFacing = 35;
    public int customLean = 10;
    /** What each trainer does with their arms in the overlay. */
    public ArmPose ownArms = ArmPose.NATURAL;
    public ArmPose opponentArms = ArmPose.NATURAL;
    /** How each trainer holds their head (the face itself is part of the skin and can't change). */
    public HeadPose ownHead = HeadPose.STRAIGHT;
    public HeadPose opponentHead = HeadPose.STRAIGHT;
    /** Size of the 3D trainer in the strip, in percent. */
    public int trainerSize = 100;
    /** Moves the trainer in the strip down (positive) or up (negative), in pixels. 0 puts the head at the top of the strip. */
    public int trainerOffset = 4;
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
    /** Hide the floating name tag above an opponent the overlay shows (the overlay already shows their name). */
    public boolean hideOpponentNameTag = true;
    /** Which Pokémon the strip shows next to the trainer. */
    public TeamLayout teamLayout = TeamLayout.ACTIVE_ONLY;
    /** For TeamLayout.MY_PICKS: UUIDs of your own Pokémon to show, chosen in the config screen. */
    public List<String> pickedPokemon = new ArrayList<>();
    /** Send your full team to opponents in the same battle who also have the mod (off: they only see what you send out). */
    public boolean shareTeam = false;
    /** 0-100 */
    public int opacity = 92;
    /** ARGB overrides; 0 means "use theme default". */
    public int accentOwn = 0;
    public int accentOpponent = 0;

    public Anchor anchorFor(boolean own) { return own ? ownAnchor : opponentAnchor; }

    public Stance stanceFor(boolean own) { return own ? ownStance : opponentStance; }

    public ArmPose armsFor(boolean own) { return own ? ownArms : opponentArms; }

    public HeadPose headFor(boolean own) { return own ? ownHead : opponentHead; }

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
                if (c != null) return c;
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
