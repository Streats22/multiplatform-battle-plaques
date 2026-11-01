# Cobblemon - Battleplacks

Client-side Cobblemon addon that replaces the per-Pokémon battle tiles with one compact **plaque per trainer**: head, name, party balls, and the active Pokémon (portrait, level, HP, status). Works in PvP, NPC and wild battles, on **Fabric and NeoForge** (Minecraft 1.21.1).

- Render preview: open `preview/index.html` (all settings are live, and the JSON it prints is the real config format)
- Research and design notes: `docs/RESEARCH.md`
- UI art: the strip background is `assets/battleplacks/textures/gui/strip.png` (140x44, replaceable by a resource pack); `docs/CHATGPT_HANDOVER.md` has a ready prompt to generate better art

## Screens

Design reference (from you): Cobblemon's in-battle HUD with a **team card per corner**: the Cobblemon-style Pokémon tile on top, your 3D player standing next to their Pokémon below it, a segmented party bar, and a "Team <name>" label. Single player means **Radical Cobblemon Trainers (RCT)** battles.

- **In-battle team cards**: the main design, implemented in `PlaqueRenderer` (Pokémon tile with Cobblemon's own 3D portrait, 3D trainer, party bar, team label). Preview default.

- **VS intro**: optional fullscreen screen shown at battle start (**preview only**, not in the mod).

## Behaviour

- Cobblemon's own tile always stays; Battleplacks draws a strip underneath it (cropped trainer and Pokémon, party bar, team label).
- The strip fades away after a few seconds (Mods -> Battleplacks -> Timing: "Fade strip", "Strip visible for"). The 3D models cannot be made transparent, so while fading the strip also slides off its screen edge.
- Optional battle intro (off by default) shows a larger presentation of both trainers first.
- Textures in `assets/battleplacks/textures/gui/` (`strip.png`, `party_bar.png`, `intro_banner.png`, plus `*_4x.png` masters) can be replaced by a resource pack. Only `strip.png` (optional background) and `party_bar.png` are used so far.

## Customizing your look

Everything below is in the Cloth Config screen. Three ways to open it, also during a battle:

- **Mod Menu** -> Battleplacks -> config button
- **Key binding** "Open Battleplacks settings" (Controls -> Battleplacks). **Unbound by default**; a key binding only fires while no screen is open, so with Cobblemon's battle menu up use the command instead.
- **Command** `/battleplacks config` (client-side, works in chat during a battle; the screen opens on top of the battle screen and returns to it when closed)

| Option (page) | What | Default |
|---|---|---|
| Team name (General) | Label text, max 20 characters, profanity filtered. Empty shows "Team <name>" | empty |
| Your accent (Look) | Accent colour of your party bar and card. 0 = theme colour | 0 (theme) |
| Bar style (Look) | `PIPS` Cobblemon's own bar with slanted pips, `SEGMENTED` plain bar with straight segments, `BALLS` poke balls | `PIPS` |
| Label position (Look) | Team label `ABOVE` or `BELOW` the party bar | `BELOW` |
| Pokemon size (Strip) | Pokemon in the strip, % of the base size | 100% |
| Pokemon height (Strip) | Pixels the Pokemon is raised (negative lowers it) | 11 px |
| Trainer size (Strip) | 3D trainer, % of the base size | 100% |
| Trainer height (Strip) | Pixels the trainer is raised (negative lowers it) | 0 px |

The Pokemon and trainer values are there because their right values can only be judged in-game; tune them and send the numbers back if the defaults are off.

### What other players see

Your team name, accent colour and bar style are shared with the other players in the battle (label position and the size/height values stay local). Each player's card is drawn with *their* shared look; if none was received (they lack the mod, or the server does not have Battleplacks), their card uses your theme colour and your bar style. NPC trainers (RCT) always use theme colours.

Sharing needs Battleplacks on the server too (it works in single player). The server and every receiving client validate the same way: the sender's own UUID is used (never one from the packet), the name must pass the length and profanity rules (otherwise it falls back to the default label), the colour must be a plain RGB value (otherwise the theme colour is used) and an unknown bar style becomes `PIPS`. Rules live in `team/AppearanceRules`.

## Layout

| Path | What |
|---|---|
| `common/` | Shared code: `PlaqueRenderer` (vanilla GuiGraphics), `PlaqueConfig`, `CobblemonBattleAdapter`, two mixins |
| `fabric/`, `neoforge/` | Thin loader entry points and metadata |
| `preview/index.html` | Browser mock of the HUD |

## How it hooks in

1. `GuiMixin` (vanilla `Gui#render`, TAIL) draws the plaques, which is identical on both loaders.
2. `CobblemonClientMixin` cancels Cobblemon's own `beforeChatRender` only when Battleplacks' own tile is switched on (`showTiles`); by default Cobblemon's tile stays and the strip is drawn under it.
3. `CobblemonBattleAdapter` is the only class that reads Cobblemon's client battle classes.

## Status

- **Compiles.** `common` and `fabric` build with Gradle 9.3.1 and Java 21 against Cobblemon 1.8.4 (jars in `libs/`, symlinked to ~/Downloads). The mod jar is `dist/battleplacks-fabric-0.1.0.jar`.
- **Code structure:** `Battleplacks` (composition root) -> `BattleSource` (interface; `CobblemonBattleSource` is the only class touching Cobblemon) -> `HudRenderer` -> `CardFactory` builds `CardElement`s (tile, trainer group, party bar/balls, label). Themes, anchors and stances are enums that carry their own data.
- **Dev client (`:fabric:runClient`) now boots.** Cobblemon 1.8.4 and Battleplacks both load, and the integrated world starts. Two fixes made that work: the Kotlin Gradle plugin (lets Loom remap Cobblemon's Kotlin metadata) and Fabric API on `common` too (Cobblemon is remapped once and cached, and needs Fabric API's class hierarchy). If you change those, clear `.gradle*/**/remapped_mods`.
- **Last known issue:** in the last run the game exited right after the world loaded, with a native crash in LWJGL/GLFW (`fabric/run/hs_err_*.log`, macOS 27, LWJGL 3.3.3). Not yet known whether that is the window being closed or a real LWJGL problem on this macOS version. The battle HUD has still not been seen in-game.
- **NeoForge does not build yet.** It is switched off in `settings.gradle.kts` (enable with `-PwithNeoForge`); Gradle fails resolving the macOS LWJGL natives.
- Visual details marked `TODO(visual)` (portrait scale, 3D trainer baseline) need an in-game check.

## Local jars (not in the repo)

`libs/` is not in the repo (it holds large third-party jars) and the build works without it. Only for RCT trainer battles in the Fabric dev client, put `rctapi-fabric-1.21.1-0.16.2-beta.jar` and `rctmod-fabric-1.21.1-0.19.2-beta.jar` in `libs/`; they are added when the files exist. Cobblemon, Fabric API, Architectury API, Cloth Config and Mod Menu (`com.terraformersmc:modmenu:11.0.5`) come from Maven. The trainer pack `MoreRadicalTrainers1.8.zip` goes in a world's `datapacks` folder.

## Build

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21 GRADLE_USER_HOME="$PWD/.gradle-home"
./gradlew --no-daemon :fabric:build   # output: fabric/build/libs/ (wrapper pins Gradle 9.3.1)
./gradlew :common:test                # unit tests for the pure logic
```

## Install to test (Fabric)

Fabric Loader 0.17.2+, Minecraft 1.21.1, plus these in the `mods` folder: Cobblemon 1.8.4, Fabric API, Fabric Language Kotlin, and `dist/battleplacks-fabric-0.1.0.jar`. Start a battle (a wild one is the quickest); config is written to `config/battleplacks.json`.

The mod is named "Battleplacks" as you wrote it; "Battleplaques" is the usual spelling, and the name is only set in a few files.
