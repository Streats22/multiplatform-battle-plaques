package dev.battleplacks.client.cobblemon;

import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.battleplacks.client.PlaqueModel.Slot;
import dev.battleplacks.client.PlaqueModel.SlotState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Turns an actor into party slots. The local player's party is read in full; for everyone else only what the
 * battle has already revealed is kept, so hidden information is never read.
 */
final class PartyReader {
    private static final int PARTY_SIZE = 6;

    /** Revealed opponent Pokémon per actor, kept so a slot stays revealed after the Pokémon switches out. */
    private final Map<UUID, Map<UUID, Slot>> revealed = new HashMap<>();
    private UUID currentBattle;

    void startBattle(UUID battleId) {
        if (!battleId.equals(currentBattle)) {
            revealed.clear();
            currentBattle = battleId;
        }
    }

    List<Slot> own(ClientBattleActor actor) {
        List<Slot> slots = new ArrayList<>();
        for (Pokemon pokemon : actor.getPokemon()) {
            boolean statused = pokemon.getStatus() != null && pokemon.getStatus().getStatus() != null;
            slots.add(new Slot(pokemon.isFainted() ? SlotState.FAINTED : statused ? SlotState.STATUS : SlotState.HEALTHY));
        }
        return padded(slots);
    }

    List<Slot> revealed(ClientBattleActor actor) {
        Map<UUID, Slot> seen = revealed.computeIfAbsent(actor.getUuid(), id -> new HashMap<>());
        for (ActiveClientBattlePokemon active : actor.getActivePokemon()) {
            ClientBattlePokemon p = active.getBattlePokemon();
            if (p == null) continue;
            SlotState state = p.getHpValue() <= 0 ? SlotState.FAINTED
                    : p.getStatus() != null ? SlotState.STATUS : SlotState.HEALTHY;
            seen.put(p.getUuid(), new Slot(state));
        }
        return padded(new ArrayList<>(seen.values()));
    }

    private static List<Slot> padded(List<Slot> slots) {
        while (slots.size() < PARTY_SIZE) slots.add(new Slot(SlotState.UNKNOWN));
        return slots.size() > PARTY_SIZE ? slots.subList(0, PARTY_SIZE) : slots;
    }
}
