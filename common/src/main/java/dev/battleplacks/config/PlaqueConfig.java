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
    public boolean stripFades = true;
    public int stripSeconds = 3;
    public boolean introEnabled = false;
    public int introSeconds = 3;
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
    /** ARGB overrides; 0 means "use theme default". */
    public int accentOwn = 0;
    public int accentOpponent = 0;

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
