# Plan: showing more than one Pokémon (team display)

Status: all four steps built on 2026-11-03 (both loaders compile); not yet tested in-game, and team sharing needs
two players to test. Checked against Cobblemon 1.8.4 (Fabric jar, `javap`).

## What was decided

- **Layout is a setting**, with four choices: *Active only* (today), *Active + bench*, *Full team*, *My picks*.
- **Opponent's team:** only Pokémon that have been seen in this battle, plus **opt-in sharing**: a player
  with Battleplacks can choose to show their full team to opponents who also have it.
  NPC (RCT) trainers' teams are never revealed early.

## What Cobblemon gives the client

| | What the client knows | Source |
|---|---|---|
| Your team | Every party Pokémon (species, form/aspects, HP, status, fainted) | `BattleSetTeamPokemonPacket` → `ClientBattleActor.getPokemon()` (own actor only) |
| Your party outside battle | The same, for the config screen's "My picks" list | `CobblemonClient.storage` (`ClientStorageManager.myParty`) |
| Opponent | Only the Pokémon currently on the field | `BattleInitializePacket` / switch packets → `getActivePokemon()`; `getPokemon()` stays empty for other actors |

So an opponent's unseen Pokémon cannot be shown without them sharing it (that is what the opt-in is for).

Drawing a Pokémon that is not on the field works like Cobblemon's own `PartyOverlay`: keep one `FloatingState`
per Pokémon UUID, call `setCurrentAspects(pokemon.getAspects())`, then `GuiUtilsKt.drawPosablePortrait(...)`
with that state, which is the same call `PokemonPortraits` already makes.

## Building it, in steps (each step ships on its own)

1. **Team model.** Replace `PlaqueModel.party` (`List<Slot>`) with `List<TeamMember>`:
   `TeamMember(SlotState state, boolean active, PortraitDrawer portrait /* null when unknown */)`.
   The party bar keeps working by reading `state` from it.
   - Own side: build members from `actor.getPokemon()`; portraits from a per-UUID `FloatingState` cache
     (new `PartyPortraits`, cleared when the battle ends).
   - Opponent: `PartyReader.revealed` keeps the `ClientBattlePokemon` it saw (not just its state), so a
     Pokémon that switched out can still be drawn. Unseen slots stay `UNKNOWN` with no portrait.
2. **Layout setting** `teamLayout` = `ACTIVE_ONLY` (default), `ACTIVE_AND_BENCH`, `FULL_TEAM`, `MY_PICKS`, on the
   Pokémon page. `StripElement` asks a small layout helper for (member, x, top, size) per Pokémon:
   - *Active + bench*: active at today's size; bench as small (~12 px) portraits in a row on the far side of
     the active one. If six don't fit in the width, the bench wraps under, or the strip grows by one row.
   - *Full team*: six equal small portraits next to the trainer, the active one(s) marked (accent outline or
     slightly bigger); fainted ones dimmed.
   - Unknown opponent slots draw as a grey "?" circle, the same as the party bar's unknown slot.
3. **My picks.** Config field `pickedPokemon` = list of Pokémon UUIDs. The config screen lists your current party
   by name (from `CobblemonClient.storage`) with a toggle each. Picked Pokémon that aren't in the battle team are
   skipped. On the opponent's side "My picks" falls back to *Active + bench*.
4. **Opt-in team sharing.** Setting "Show my team to opponents" (off by default). New payload
   `TeamPreviewPayload(player, List<species id + aspects + state>)`. The client sends it when a battle starts
   (only if opted in); the server relays it only to players **in the same battle** (an extension of
   `TeamNameRelay`); the receiver merges it into the opponent's members. Re-checked on the receiving side:
   ignored outside a battle with that player. NPCs never send one.

## Things to watch

- Performance: up to 12 extra 3D models per frame. Cobblemon's party overlay draws 6, so this should be fine,
  but cache `FloatingState`s and skip drawing what is clipped.
- Space: the strip is 140 × 44 GUI px and the trainer takes about a third; the bench/full-team sizes above
  need an in-game check, like the active Pokémon did.
- Doubles: two active Pokémon already exist (`model.active()` with 2 entries); layouts must handle them.
- Multiplayer fairness: never read another player's party from anywhere but the opt-in payload.
