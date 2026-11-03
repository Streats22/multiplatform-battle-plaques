package dev.battleplacks.team;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.BattleRegistry;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server side of team sharing: a player who opted in sends their team when a battle starts, and it goes to the
 * other players in that same battle only. The battle id in the packet must be the sender's current battle, and the
 * sender's own id is used, so nobody can share a team for someone else or for another battle.
 */
public final class TeamPreviewRelay {
    private final PayloadSender sender;

    public TeamPreviewRelay(PayloadSender sender) { this.sender = sender; }

    public void received(ServerPlayer from, TeamPreviewPayload payload) {
        PokemonBattle battle = BattleRegistry.getBattleByParticipatingPlayer(from);
        if (battle == null || !battle.getBattleId().equals(payload.battle())) return;
        TeamPreviewPayload relayed = new TeamPreviewPayload(from.getUUID(), battle.getBattleId(), payload.team());
        for (ServerPlayer other : battle.getPlayers()) {
            if (!other.getUUID().equals(from.getUUID())) sender.send(other, relayed);
        }
    }
}
