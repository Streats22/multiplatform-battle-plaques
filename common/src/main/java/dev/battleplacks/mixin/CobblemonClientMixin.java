package dev.battleplacks.mixin;

import dev.battleplacks.Battleplacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels Cobblemon's own battle tiles while Battleplacks is active. Target verified in Cobblemon 1.8.4:
 * beforeChatRender(GuiGraphics, float), called from a Gui mixin on Fabric and from RenderGuiLayerEvent.Pre
 * (chat layer) on NeoForge. It also draws the party overlay outside battles, so we only cancel in a battle.
 */
@Mixin(targets = "com.cobblemon.mod.common.client.CobblemonClient", remap = false)
public abstract class CobblemonClientMixin {
    @Inject(method = "beforeChatRender", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void battleplacks$replaceTiles(CallbackInfo ci) {
        if (Battleplacks.get().replacesDefaultTiles()) ci.cancel();
    }
}
