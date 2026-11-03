package dev.battleplacks.neoforge;

import dev.battleplacks.Battleplacks;
import dev.battleplacks.config.screen.ConfigScreens;
import dev.battleplacks.team.TeamNamePayload;
import dev.battleplacks.team.TeamNames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

/** Client side: sends our team name on join and when settings are saved, and adds the config button in the mod list. */
final class BattleplacksNeoForgeClient {
    private BattleplacksNeoForgeClient() {}

    static void init(ModContainer container) {
        Battleplacks.get().setTeamNameSync(name -> {
            Minecraft mc = Minecraft.getInstance();
            ClientPacketListener connection = mc.getConnection();
            if (mc.player != null && connection != null && connection.hasChannel(TeamNamePayload.TYPE)) {
                PacketDistributor.sendToServer(new TeamNamePayload(mc.player.getUUID(), name));
            }
        });
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, event -> Battleplacks.get().announceTeamName());
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, event -> TeamNames.clear());

        // Without Cloth Config the button is simply not offered
        if (ModList.get().isLoaded("cloth_config")) {
            container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> ConfigScreens.create(parent));
        }
    }
}
