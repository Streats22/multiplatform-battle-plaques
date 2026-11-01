package dev.battleplacks.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.battleplacks.config.screen.ConfigScreens;
import net.fabricmc.loader.api.FabricLoader;

/** Adds the config button in Mod Menu. Without Cloth Config the button is simply not offered. */
public final class BattleplacksModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) return ModMenuApi.super.getModConfigScreenFactory();
        return ConfigScreens::create;
    }
}
