package dev.battleplacks.fabric;

import dev.battleplacks.Battleplacks;
import dev.battleplacks.team.SharedTeams;
import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNames;
import dev.battleplacks.team.TeamPreviewPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Client side of sharing: sends our team name (and team, if opted in), stores what other players share with us. */
public final class BattleplacksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Battleplacks.get().setSender(payload -> {
            if (ClientPlayNetworking.canSend(payload.type())) ClientPlayNetworking.send(payload);
        });
        ClientPlayNetworking.registerGlobalReceiver(TeamNamePayload.TYPE,
                (payload, context) -> TeamNames.set(payload.player(), payload.name()));
        ClientPlayNetworking.registerGlobalReceiver(TeamPreviewPayload.TYPE,
                (payload, context) -> SharedTeams.set(payload.player(), payload.battle(), payload.team()));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> Battleplacks.get().announceTeamName());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            TeamNames.clear();
            SharedTeams.clear();
        });
    }
}
