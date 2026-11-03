package dev.battleplacks.mixin;

import dev.battleplacks.client.render.TrainerPoseOverride;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla class, so identical on Fabric and NeoForge. Players and RCT trainers both use HumanoidModel (PlayerModel
 * copies its sleeves from the arms after this runs, so they follow). Only changes anything while the overlay draws.
 */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void battleplacks$overlayPose(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                          float netHeadYaw, float headPitch, CallbackInfo ci) {
        TrainerPoseOverride.apply((HumanoidModel<?>) (Object) this, ageInTicks);
    }
}
