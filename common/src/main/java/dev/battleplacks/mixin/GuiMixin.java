package dev.battleplacks.mixin;

import dev.battleplacks.Battleplacks;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla class, so this mixin is identical on Fabric and NeoForge. */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void battleplacks$render(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        Battleplacks.get().render(graphics, delta.getGameTimeDeltaPartialTick(true));
    }
}
