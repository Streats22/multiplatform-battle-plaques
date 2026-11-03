package dev.battleplacks.client.cobblemon;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.client.battle.ActiveClientBattlePokemon;
import com.cobblemon.mod.common.client.battle.ClientBattleActor;
import com.cobblemon.mod.common.client.battle.ClientBattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import dev.battleplacks.client.PlaqueModel.PortraitDrawer;
import dev.battleplacks.client.PlaqueModel.SlotState;
import dev.battleplacks.client.PlaqueModel.TeamMember;
import dev.battleplacks.team.SharedTeams;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Turns an actor into its team. The local player's party is read in full. For everyone else only what the battle
 * has revealed is kept, plus a team the opponent chose to share with us (SharedTeams), so hidden information is
 * never read.
 */
final class PartyReader {
    private static final int PARTY_SIZE = 6;

    private final PokemonPortraits portraits;
    /** Opponent Pokémon seen in this battle, per actor, in the order they appeared; kept after they switch out. */
    private final Map<UUID, Map<UUID, ClientBattlePokemon>> revealed = new HashMap<>();
    private UUID currentBattle;

    PartyReader(PokemonPortraits portraits) {
        this.portraits = portraits;
    }

    void startBattle(UUID battleId) {
        if (!battleId.equals(currentBattle)) {
            revealed.clear();
            portraits.clear();
            currentBattle = battleId;
        }
    }

    /** Our own team, in party order. Pokémon on the field use their battle portrait, the rest a bench portrait. */
    List<TeamMember> own(ClientBattleActor actor) {
        Map<UUID, ClientBattlePokemon> onField = onField(actor);
        List<TeamMember> team = new ArrayList<>();
        for (Pokemon pokemon : actor.getPokemon()) {
            SlotState state = stateOf(pokemon);
            ClientBattlePokemon field = onField.get(pokemon.getUuid());
            PortraitDrawer portrait = field != null ? portraits.of(field)
                    : portraits.of(pokemon.getUuid(), pokemon.getSpecies(), pokemon.getAspects());
            team.add(new TeamMember(pokemon.getUuid(), state, field != null, portrait));
        }
        return padded(team);
    }

    /** Our own team as we share it with opponents who have the mod (only when the player opted in). */
    List<SharedTeams.Member> shareable(ClientBattleActor actor) {
        List<SharedTeams.Member> team = new ArrayList<>();
        for (Pokemon pokemon : actor.getPokemon()) {
            team.add(new SharedTeams.Member(pokemon.getUuid(), pokemon.getSpecies().getResourceIdentifier().toString(),
                    pokemon.getAspects(), stateOf(pokemon)));
        }
        return team.size() > PARTY_SIZE ? team.subList(0, PARTY_SIZE) : team;
    }

    private static SlotState stateOf(Pokemon pokemon) {
        boolean statused = pokemon.getStatus() != null && pokemon.getStatus().getStatus() != null;
        return pokemon.isFainted() ? SlotState.FAINTED : statused ? SlotState.STATUS : SlotState.HEALTHY;
    }

    /** An opponent's team: what we have seen, merged into their shared team when they shared one. */
    List<TeamMember> opponent(ClientBattleActor actor) {
        Map<UUID, ClientBattlePokemon> onField = onField(actor);
        Map<UUID, ClientBattlePokemon> seen = revealed.computeIfAbsent(actor.getUuid(), id -> new LinkedHashMap<>());
        seen.putAll(onField);

        List<TeamMember> team = new ArrayList<>();
        Map<UUID, ClientBattlePokemon> notListed = new LinkedHashMap<>(seen);
        for (SharedTeams.Member shared : SharedTeams.get(actor.getUuid(), currentBattle).orElse(List.of())) {
            ClientBattlePokemon battle = notListed.remove(shared.id());
            if (battle != null) {
                team.add(fromBattle(battle, onField.containsKey(battle.getUuid())));
            } else {
                Species species = PokemonSpecies.getByIdentifier(ResourceLocation.tryParse(shared.species()));
                PortraitDrawer portrait = species == null ? null : portraits.of(shared.id(), species, shared.aspects());
                team.add(new TeamMember(shared.id(), shared.state(), false, portrait));
            }
        }
        for (ClientBattlePokemon battle : notListed.values()) team.add(fromBattle(battle, onField.containsKey(battle.getUuid())));
        return padded(team);
    }

    private TeamMember fromBattle(ClientBattlePokemon p, boolean active) {
        SlotState state = p.getHpValue() <= 0 ? SlotState.FAINTED
                : p.getStatus() != null ? SlotState.STATUS : SlotState.HEALTHY;
        return new TeamMember(p.getUuid(), state, active, portraits.of(p));
    }

    private static Map<UUID, ClientBattlePokemon> onField(ClientBattleActor actor) {
        Map<UUID, ClientBattlePokemon> field = new LinkedHashMap<>();
        for (ActiveClientBattlePokemon slot : actor.getActivePokemon()) {
            ClientBattlePokemon p = slot.getBattlePokemon();
            if (p != null) field.put(p.getUuid(), p);
        }
        return field;
    }

    private static List<TeamMember> padded(List<TeamMember> team) {
        while (team.size() < PARTY_SIZE) team.add(TeamMember.unknown());
        return team.size() > PARTY_SIZE ? team.subList(0, PARTY_SIZE) : team;
    }
}
