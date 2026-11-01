# Cobblemon - Battleplacks

Client-side Cobblemon addon that replaces the per-Pokémon battle tiles with one compact **plaque per trainer**: head, name, party balls, and the active Pokémon (portrait, level, HP, status). Works in PvP, NPC and wild battles, on **Fabric and NeoForge** (Minecraft 1.21.1).

- Render preview: open `preview/index.html` (all settings are live, and the JSON it prints is the real config format)
- Research and design notes: `docs/RESEARCH.md`
- UI art: the strip background is `assets/battleplacks/textures/gui/strip.png` (140x44, replaceable by a resource pack); `docs/CHATGPT_HANDOVER.md` has a ready prompt to generate better art

## Screens

Design reference (from you): Cobblemon's in-battle HUD with a **team card per corner**: the Cobblemon-style Pokémon tile on top, your 3D player standing next to their Pokémon below it, a segmented party bar, and a "Team <name>" label. Single player means **Radical Cobblemon Trainers (RCT)** battles.

- **In-battle team cards**: the main design, implemented in `PlaqueRenderer` (Pokémon tile with Cobblemon's own 3D portrait, 3D trainer, party bar, team label). Preview default.

- **VS intro**: optional fullscreen screen shown at battle start (**preview only**, not in the mod).

## Layout

| Path | What |
|---|---|
| `common/` | Shared code: `PlaqueRenderer` (vanilla GuiGraphics), `PlaqueConfig`, `CobblemonBattleAdapter`, two mixins |
| `fabric/`, `neoforge/` | Thin loader entry points and metadata |
| `preview/index.html` | Browser mock of the HUD |

## How it hooks in

1. `GuiMixin` (vanilla `Gui#render`, TAIL) draws the plaques, which is identical on both loaders.
2. `CobblemonClientMixin` cancels Cobblemon's own `beforeChatRender` while a battle is active (toggle: `replaceDefaultTiles`).
3. `CobblemonBattleAdapter` is the only class that reads Cobblemon's client battle classes.

## Status

- **Compiles.** `common` and `fabric` build with Gradle 9.3.1 and Java 21 against Cobblemon 1.8.4 (jars in `libs/`, symlinked to ~/Downloads). The mod jar is `dist/battleplacks-fabric-0.1.0.jar`.
- **Code structure:** `Battleplacks` (composition root) -> `BattleSource` (interface; `CobblemonBattleSource` is the only class touching Cobblemon) -> `HudRenderer` -> `CardFactory` builds `CardElement`s (tile, trainer group, party bar/balls, label). Themes, anchors and stances are enums that carry their own data.
- **Dev client (`:fabric:runClient`) now boots.** Cobblemon 1.8.4 and Battleplacks both load, and the integrated world starts. Two fixes made that work: the Kotlin Gradle plugin (lets Loom remap Cobblemon's Kotlin metadata) and Fabric API on `common` too (Cobblemon is remapped once and cached, and needs Fabric API's class hierarchy). If you change those, clear `.gradle*/**/remapped_mods`.
- **Last known issue:** in the last run the game exited right after the world loaded, with a native crash in LWJGL/GLFW (`fabric/run/hs_err_*.log`, macOS 27, LWJGL 3.3.3). Not yet known whether that is the window being closed or a real LWJGL problem on this macOS version. The battle HUD has still not been seen in-game.
- **NeoForge does not build yet.** It is switched off in `settings.gradle.kts` (enable with `-PwithNeoForge`); Gradle fails resolving the macOS LWJGL natives.
- Visual details marked `TODO(visual)` (portrait scale, 3D trainer baseline) need an in-game check.

## Local jars (not in the repo)

`libs/` is not in the repo (it holds large third-party jars). For the Fabric dev client put these in `libs/`: `rctapi-fabric-1.21.1-0.16.2-beta.jar`, `rctmod-fabric-1.21.1-0.19.2-beta.jar`, `modmenu-11.0.5.jar`. Cobblemon, Fabric API, Architectury API and Cloth Config come from Maven. The trainer pack `MoreRadicalTrainers1.8.zip` goes in a world's `datapacks` folder.

## Build

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21 GRADLE_USER_HOME="$PWD/.gradle-home"
<gradle 9.3.1>/bin/gradle --no-daemon :fabric:build   # output: fabric/build/libs/
```

## Install to test (Fabric)

Fabric Loader 0.17.2+, Minecraft 1.21.1, plus these in the `mods` folder: Cobblemon 1.8.4, Fabric API, Fabric Language Kotlin, and `dist/battleplacks-fabric-0.1.0.jar`. Start a battle (a wild one is the quickest); config is written to `config/battleplacks.json`.

The mod is named "Battleplacks" as you wrote it; "Battleplaques" is the usual spelling, and the name is only set in a few files.
