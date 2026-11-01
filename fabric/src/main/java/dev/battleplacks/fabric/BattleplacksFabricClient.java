package dev.battleplacks.fabric;

import dev.battleplacks.Battleplacks;
import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNames;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/** Client side of team name sharing: sends our name on join and when settings are saved, stores names we receive. */
public final class BattleplacksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Battleplacks.get().setTeamNameSync(name -> {
            if (Minecraft.getInstance().player != null && ClientPlayNetworking.canSend(TeamNamePayload.TYPE)) {
                ClientPlayNetworking.send(new TeamNamePayload(Minecraft.getInstance().player.getUUID(), name));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(TeamNamePayload.TYPE,
                (payload, context) -> TeamNames.set(payload.player(), payload.name()));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> Battleplacks.get().announceTeamName());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> TeamNames.clear());
    }
}
