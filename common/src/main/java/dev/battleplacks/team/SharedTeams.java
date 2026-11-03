package dev.battleplacks.team;

import dev.battleplacks.client.PlaqueModel.SlotState;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Teams that other players chose to share with us (the "Show my team to opponents" setting), by player id, each for
 * one battle. Filled from TeamPreviewPayload; only read for an opponent in that same battle.
 */
public final class SharedTeams {
    /** One shared Pokémon: its id (to match it once it is sent out), species id, form aspects and state. */
    public record Member(UUID id, String species, Set<String> aspects, SlotState state) {}

    private record Shared(UUID battle, List<Member> team) {}

    private static final Map<UUID, Shared> TEAMS = new ConcurrentHashMap<>();

    private SharedTeams() {}

    public static void set(UUID player, UUID battle, List<Member> team) {
        TEAMS.put(player, new Shared(battle, List.copyOf(team)));
    }

    /** The team {@code player} shared for {@code battle}; empty if they did not share one for that battle. */
    public static Optional<List<Member>> get(UUID player, UUID battle) {
        Shared shared = TEAMS.get(player);
        return shared != null && shared.battle().equals(battle) ? Optional.of(shared.team()) : Optional.empty();
    }

    public static void clear() { TEAMS.clear(); }
}
