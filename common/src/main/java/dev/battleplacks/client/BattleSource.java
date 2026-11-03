package dev.battleplacks.client;

import dev.battleplacks.team.SharedTeams;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Where battle state comes from. The renderer depends on this interface, never on Cobblemon directly. */
public interface BattleSource {
    boolean inBattle();

    /** Id of the running battle, used to start the intro once per battle. */
    Optional<UUID> battleId();

    /** One model per trainer, the local player first. Empty when no battle is running. */
    List<PlaqueModel> snapshot();

    /** Our own team in this battle, in the form we share it with opponents. Empty when there is none. */
    List<SharedTeams.Member> ownTeamToShare();
}
