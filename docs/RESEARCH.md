# Research notes: Cobblemon battle UI

Researched 2026-11-01. Sources are Cobblemon's GitLab (`cable-mc/cobblemon`, main), the Cobblemon wiki, and Modrinth pages. Items marked **unverified** were not confirmed from source.

## What Cobblemon draws today

Package `com.cobblemon.mod.common.client.gui.battle`:

| Class | Role |
|---|---|
| `BattleGUI` | Interactive screen: action menu, move/switch/target selection, message pane |
| `BattleOverlay` | HUD tiles for each active Pokémon (the part Battleplacks replaces) |
| `subscreen/*` | Fight, switch, target, forfeit, gimmick screens (left alone) |

Client battle model, package `com.cobblemon.mod.common.client.battle`:

- `ClientBattle`: `side1`, `side2`, `battleFormat`, `spectating`, `wildActor`, `isPvW`
- `ClientBattleSide`: `actors`, `activeClientBattlePokemon`
- `ClientBattleActor`: `displayName`, `uuid`, `type` (`ActorType`), `pokemon` (party, full `Pokemon` objects), `activePokemon`
- `ClientBattlePokemon`: `displayName`, `species`, `level`, `gender`, `hpValue`, `maxHp`, `isHpFlat`, `status`, `statChanges`. **No held item, no ball field.**

`BattleOverlay` constants: `TILE_WIDTH=140`, `TILE_HEIGHT=40`, compact `128x28`, portrait 28 px (compact 19 px). It renders only when `CobblemonClient.battle != null`, shows the actor name only in non-wild battles, and draws **no party row**. That gap is what Battleplacks fills.

Textures live in `assets/cobblemon/textures/gui/battle/` (`battle_info_base*.png`, `battle_info_role*.png`, `battle_status_*.png`, `battle_owned_indicator.png`, ...). Resource packs can replace them (confirmed by existing packs such as Cobblemon GUI KDS), but those packs are all-rights-reserved, so Battleplacks ships its own art.

## Build and hook facts

- Cobblemon mod id `cobblemon`, 1.8.5 (snapshot), Minecraft **1.21.1**, Java 21, Kotlin 2.2.20
- Layout `common` + `fabric` + `neoforge` (Architectury Loom). Fabric loader 0.17.2, Fabric API 0.116.6+1.21.1, NeoForge 21.1.195
- Fabric draws the overlay via a `Gui#renderCameraOverlays` mixin that calls `CobblemonClient.beforeChatRender`, which calls `battleOverlay.render(...)` when a battle is active
- On NeoForge the call site of `beforeChatRender` is **unverified**

**Design consequence:** mixin `CobblemonClient.beforeChatRender` from `common`. It is Cobblemon's own class, so one mixin works on both loaders and we never touch loader-specific HUD events.

## Existing competition

[Cobblemon: Extended Battle UI](https://modrinth.com/mod/ry6cHJdV): Fabric only, client only, ~83K downloads. Info panel (weather, field, stat stages), colour-coded battle log, mini team models, move tooltips. Dense multi-panel layout, no compact mode, no NeoForge build.

Battleplacks differentiators: Fabric **and** NeoForge, one compact "plaque" per trainer, player head and party always visible, themes and layout fully configurable.

## What the plaque shows

Per trainer (you, PvP opponent, NPC; wild Pokémon get a plain tile):

1. Head/skin face and name (NPC uses a generic face)
2. Party row of up to 6 balls: healthy, statused, fainted, empty/unknown
3. Active Pokémon tile(s): portrait, name, level, gender, HP bar and text, status

Visibility rules (fair play, matches Extended Battle UI's stance): your own party is fully shown. For an opponent we only show Pokémon already revealed in battle, plus unknown slots for the rest. **Unverified:** whether `ClientBattleActor.pokemon` is filled for opponents, so the renderer must not rely on it for them.

## Single player = Radical Cobblemon Trainers (RCT)

Single-player battles in Battleplacks mean RCT trainers, not wild Pokémon (wild is still handled as a fallback).

- RCT trainers are **not** a custom entity. rctapi starts a standard Cobblemon `PokemonBattle` (`BattleManager.startBattle`); the trainer side is `TrainerEntityBattleActor`, whose `getType()` is **`ActorType.NPC`**. So the client sees a normal `ClientBattle` with an NPC `ClientBattleActor`, and `CobblemonBattleAdapter` already covers it.
- Trainer JSON has name, ai, bag, team, battleTheme, battleFormat (`GEN_9_SINGLES/DOUBLES/TRIPLES/MULTI/ROYAL`). Trainer type, series and skin texture belong to rctmod's data packs.
- **Unverified:** whether rctmod syncs trainer type, series or texture to the client. Until confirmed, the preview's series subtitle (e.g. "Pewter Gym") is a design idea; the real mod can only rely on the actor's display name.
- Maven: rctapi via cursemaven `curse.maven:radical-cobblemon-trainers-api-1152792:<fileId>` (0.16.2-beta, min Cobblemon 1.8.2). rctmod has no maven coordinates found (0.19.2-beta).
- RCT events (`BATTLE_STARTED`, `BATTLE_ENDED`) are server-side only; a client HUD does not need them.

## Verified against the real jars (Cobblemon 1.8.4 for 1.21.1, Fabric and NeoForge)

Read with `javap` from the jars you supplied:

- `ActorType` = `WILD`, `PLAYER`, `NPC`; `Gender` = `MALE`, `FEMALE`, `GENDERLESS`
- `CobblemonClient.beforeChatRender(GuiGraphics, float)` exists. Fabric calls it from a `Gui` mixin; **NeoForge calls it from `RenderGuiLayerEvent.Pre` on the CHAT layer** (`CobblemonNeoForgeClient`). One mixin on `CobblemonClient` therefore works on both loaders.
- `GuiUtilsKt.drawPosablePortrait(ResourceLocation, PoseStack, float scale, float contextScale, boolean reversed, PosableState, float partialTicks)` is a real Java overload (Kotlin `@JvmOverloads`); `ClientBattlePokemon.getState()` is a `FloatingState` and `PosableState.getCurrentAspects()` exists
- `Pokemon`: `isFainted()`, `getStatus()` (a `PersistentStatusContainer`, `.getStatus()` gives the `PersistentStatus`), `getSpecies()`, `getCurrentHealth()`, `getMaxHealth()`
- `ClientBattleActor.getPokemon()` is a `List<Pokemon>`; it is the local player's party (opponents' lists must not be relied on)

## Open items before shipping

1. **Never compiled or run in-game.** The signatures are checked, the code is not. Next step is wiring the jars as `modCompileOnly` and building.
2. Visual tuning that only an in-game run can confirm (`TODO(visual)` in the code): portrait scale and offset inside the tile frame, and where the 3D trainer's feet land on the baseline
3. Whether the trainer entity UUID in an RCT battle matches the actor UUID (the 3D trainer falls back to a grey block if no entity is found)
4. Whether rctmod syncs trainer series/skin to the client (series subtitles in the preview are a design idea)
5. Maven coordinates for Cobblemon (the Gradle dependency lines are still commented out)
