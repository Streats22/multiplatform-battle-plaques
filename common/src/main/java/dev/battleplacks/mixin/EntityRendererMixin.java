package dev.battleplacks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.battleplacks.Battleplacks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hides the floating name tag of an opponent the overlay is showing (the overlay already shows their name, and an
 * NPC standing in front of the camera puts its tag in the middle of the screen). Vanilla class; RCT's
 * TrainerRenderer does not override renderNameTag, so this covers players and NPC trainers alike.
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void battleplacks$hideOpponentTag(T entity, Component name, PoseStack pose, MultiBufferSource buffers,
                                              int light, float partialTick, CallbackInfo ci) {
        if (Battleplacks.get().hidesNameTag(entity.getUUID())) ci.cancel();
    }
}
