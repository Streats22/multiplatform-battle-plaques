package dev.battleplacks.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.Command;
import dev.battleplacks.Battleplacks;
import dev.battleplacks.team.AppearancePayload;
import dev.battleplacks.team.Appearances;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import dev.battleplacks.config.screen.ConfigScreens;

/**
 * Client side: shares our appearance (on join and when settings are saved), stores the ones we receive, and offers
 * three ways into the config screen: Mod Menu, a key binding (unbound) and {@code /battleplacks config}.
 */
public final class BattleplacksFabricClient implements ClientModInitializer {
    /** A command runs while the chat screen is still open (which closes itself afterwards), so opening is deferred to the next tick. */
    private boolean openRequested;

    @Override
    public void onInitializeClient() {
        registerAppearanceSync();
        registerConfigAccess();
    }

    private void registerAppearanceSync() {
        Battleplacks.get().setAppearanceSync(look -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && ClientPlayNetworking.canSend(AppearancePayload.TYPE)) {
                ClientPlayNetworking.send(AppearancePayload.of(mc.player.getUUID(), look));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(AppearancePayload.TYPE, (payload, context) -> {
            // Re-validated in Appearances.set, whatever the server (or someone pretending to be one) sent.
            if (payload.clear()) Appearances.remove(payload.player());
            else Appearances.set(payload.player(), payload.name(), payload.accentRgb(), payload.styleId());
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> Battleplacks.get().announceAppearance());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> Appearances.clear());
    }

    private void registerConfigAccess() {
        KeyMapping key = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.battleplacks.config", InputConstants.UNKNOWN.getValue(), "key.categories.battleplacks"));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("battleplacks")
                        .then(ClientCommandManager.literal("config").executes(context -> {
                            openRequested = true;
                            return Command.SINGLE_SUCCESS;
                        }))));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (key.consumeClick()) openRequested = true;
            if (openRequested) {
                openRequested = false;
                openConfig(client);
            }
        });
    }

    /** Opens the config screen on top of whatever is showing, so closing it returns to the battle screen. */
    private static void openConfig(Minecraft client) {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            if (client.player != null) client.player.displayClientMessage(
                    Component.translatable("message.battleplacks.needs_cloth_config"), false);
            return;
        }
        client.setScreen(ConfigScreens.create(client.screen));
    }
}
